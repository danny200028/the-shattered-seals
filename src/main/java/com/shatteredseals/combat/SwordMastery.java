package com.shatteredseals.combat;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Five cosmetic ranks for each existing legendary blade; no passive power changes. */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class SwordMastery {
    private static final String SAVE = "SSSwordMastery";
    private static final String[] IDS = {"emberbound", "kings_fang", "wardens_oath", "starfall", "gravekeeper", "kings_bane"};
    private static final String[] NAMES = {"Emberbound", "King's Fang", "Warden's Oath", "Starfall", "Gravekeeper", "King's Bane"};
    private static final int[] THRESHOLDS = {8, 20, 40, 70, 110};

    public static int rank(int kills) {
        int rank = 0;
        for (int threshold : THRESHOLDS) if (kills >= threshold) rank++;
        return rank;
    }
    public static int[] snapshot(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(SAVE);
        int[] result = new int[IDS.length];
        for (int i = 0; i < IDS.length; i++) result[i] = data.getInt(IDS[i]);
        return result;
    }
    @SubscribeEvent
    public static void onMeleeVictory(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getSource().getDirectEntity() != player
                || !(event.getEntity() instanceof Mob victim)
                || !(victim instanceof Enemy) || victim.getMaxHealth() < 18.0F
                || victim.hasCustomName() || victim.getTags().stream().anyMatch(s -> s.startsWith("ss_"))) return;
        var weapon = player.getMainHandItem();
        if (!weapon.is(ItemTags.SWORDS)) return;
        CustomData custom = weapon.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return;
        CompoundTag item = custom.copyTag();
        if (!item.getString("ss_tier").matches("dormant|awakened|ascended")) return;
        String id = item.getString("ss_weapon");
        for (int i = 0; i < IDS.length; i++) {
            if (!IDS[i].equals(id)) continue;
            long now = player.getServer().overworld().getGameTime();
            CompoundTag data = player.getPersistentData().getCompound(SAVE);
            String lastKey = id + "_last";
            if (data.contains(lastKey) && now - data.getLong(lastKey) < 100L) return;
            data.putLong(lastKey, now);
            int previous = data.getInt(id), kills = Math.min(10000, previous + 1);
            data.putInt(id, kills);
            player.getPersistentData().put(SAVE, data);
            if (rank(previous) < rank(kills)) player.sendSystemMessage(Component.literal(
                NAMES[i] + " mastery rank " + rank(kills) + "/5 earned! Cosmetic only.")
                .withStyle(ChatFormatting.GOLD));
            return;
        }
    }
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag data = event.getOriginal().getPersistentData();
        if (data.contains(SAVE)) player.getPersistentData().put(SAVE, data.getCompound(SAVE).copy());
    }
    private SwordMastery() {}
}
