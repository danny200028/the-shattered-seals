package com.shatteredseals.combat;

import com.shatteredseals.ShatteredSeals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Five cosmetic-only mastery ranks for each existing legendary blade.
 * Rank 5 unlocks a unique held-weapon aura, a multi-tick death spiral and
 * an exclusive title. No stats, loot, damage, attributes or passive odds change.
 * The original saved kill counts are the sole unlock source, so existing 5/5
 * swords gain their cosmetics immediately without any migration or re-grind.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class SwordMastery {
    private static final String SAVE = "SSSwordMastery";
    private static final String[] IDS = {"emberbound", "kings_fang", "wardens_oath", "starfall", "gravekeeper", "kings_bane"};
    private static final String[] NAMES = {"Emberbound", "King's Fang", "Warden's Oath", "Starfall", "Gravekeeper", "King's Bane"};
    private static final String[] TITLES = {
        "Ashen Sovereign", "Royal Executioner", "Oathkeeper",
        "Starborn", "Soulbound", "Riftbreaker"
    };
    private static final int[] THRESHOLDS = {8, 20, 40, 70, 110};
    private static final SimpleParticleType[] AURA = {
        ParticleTypes.FLAME, ParticleTypes.CRIT, ParticleTypes.SOUL_FIRE_FLAME,
        ParticleTypes.END_ROD, ParticleTypes.SOUL, ParticleTypes.REVERSE_PORTAL
    };
    private static final SimpleParticleType[] FINISHER = {
        ParticleTypes.FLAME, ParticleTypes.CRIT, ParticleTypes.SOUL_FIRE_FLAME,
        ParticleTypes.END_ROD, ParticleTypes.SOUL, ParticleTypes.REVERSE_PORTAL
    };
    private static final SimpleParticleType[] FINISHER_SPARK = {
        ParticleTypes.LAVA, ParticleTypes.ENCHANT, ParticleTypes.TOTEM_OF_UNDYING,
        ParticleTypes.FIREWORK, ParticleTypes.SCULK_SOUL, ParticleTypes.PORTAL
    };
    private static final Map<UUID, List<VictoryAnimation>> ACTIVE_FINISHERS = new HashMap<>();

    public static int rank(int kills) {
        int rank = 0;
        for (int threshold : THRESHOLDS) if (kills >= threshold) rank++;
        return rank;
    }

    public static int swordCount() { return IDS.length; }
    public static String swordId(int index) { return IDS[index]; }
    public static String title(int index) { return TITLES[index]; }
    public static boolean hasCosmetics(int kills) { return rank(kills) == 5; }

    /** Works on both client and server. Excludes renamed ordinary swords. */
    public static int swordIndex(ItemStack weapon) {
        if (!weapon.is(ItemTags.SWORDS)) return -1;
        CustomData custom = weapon.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return -1;
        CompoundTag item = custom.copyTag();
        String tier = item.getString("ss_tier");
        if (!tier.equals("dormant") && !tier.equals("awakened") && !tier.equals("ascended")) return -1;
        String id = item.getString("ss_weapon");
        for (int i = 0; i < IDS.length; i++) if (IDS[i].equals(id)) return i;
        return -1;
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
                || !(victim instanceof Enemy)) return;
        int i = swordIndex(player.getMainHandItem());
        if (i < 0) return;

        String id = IDS[i];
        CompoundTag data = player.getPersistentData().getCompound(SAVE);
        // Preserve all original mastery-farming restrictions. Ordinary hostile
        // kills that don't qualify for progress can still show the unlocked FX.
        if (victim.getMaxHealth() >= 18.0F && !victim.hasCustomName()
                && victim.getTags().stream().noneMatch(s -> s.startsWith("ss_"))) {
            long now = player.getServer().overworld().getGameTime();
            String lastKey = id + "_last";
            if (!data.contains(lastKey) || now - data.getLong(lastKey) >= 100L) {
                data.putLong(lastKey, now);
                int previous = data.getInt(id);
                int kills = Math.min(10000, previous + 1);
                data.putInt(id, kills);
                player.getPersistentData().put(SAVE, data);
                if (rank(previous) < rank(kills)) {
                    player.sendSystemMessage(Component.literal(NAMES[i] + " mastery rank "
                        + rank(kills) + "/5 earned! Cosmetic only.").withStyle(ChatFormatting.GOLD));
                    if (hasCosmetics(kills))
                        player.sendSystemMessage(Component.literal("Title unlocked: " + TITLES[i]
                            + " | Sword aura and unique kill finisher unlocked.")
                            .withStyle(ChatFormatting.AQUA));
                }
            }
        }

        if (hasCosmetics(data.getInt(id))) startFinisher(player, victim, i);
    }

    private static void startFinisher(ServerPlayer player, Mob victim, int swordIndex) {
        List<VictoryAnimation> finishers = ACTIVE_FINISHERS.computeIfAbsent(
            player.getUUID(), unused -> new ArrayList<>());
        // Particle-only; limit visuals when mowing through packs of enemies.
        if (finishers.size() >= 8) finishers.remove(0);
        finishers.add(new VictoryAnimation(player.serverLevel(),
            victim.position().add(0.0, Math.min(0.8, victim.getBbHeight() * 0.4), 0.0), swordIndex));
        player.serverLevel().playSound(null, victim.blockPosition(),
            SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS,
            0.55F, 0.75F + swordIndex * 0.13F);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        List<VictoryAnimation> finishers = ACTIVE_FINISHERS.get(player.getUUID());
        if (finishers != null) {
            finishers.removeIf(animation ->
                animation.level != player.serverLevel() || animation.advance());
            if (finishers.isEmpty()) ACTIVE_FINISHERS.remove(player.getUUID());
        }

        if (!player.isAlive() || player.tickCount % 10 != 0) return;
        int i = swordIndex(player.getMainHandItem());
        if (i < 0 || !hasCosmetics(player.getPersistentData().getCompound(SAVE).getInt(IDS[i]))) return;

        // One restrained orbiting particle pair every half-second.
        // No player effects, damage, sounds or stat changes.
        double angle = player.tickCount * 0.15 + i * Math.PI / 3.0;
        double height = player.getY() + 0.8 + 0.18 * Math.sin(angle);
        ServerLevel level = player.serverLevel();
        for (int arm = 0; arm < 2; arm++) {
            double a = angle + arm * Math.PI;
            level.sendParticles(AURA[i],
                player.getX() + Math.cos(a) * 0.7, height,
                player.getZ() + Math.sin(a) * 0.7,
                1, 0.03, 0.04, 0.03, 0.005);
        }
    }

    /** Each finisher animates two spiralling trails for 12 server ticks. */
    private static final class VictoryAnimation {
        private final ServerLevel level;
        private final Vec3 at;
        private final int sword;
        private int frame = 0;

        private VictoryAnimation(ServerLevel level, Vec3 at, int sword) {
            this.level = level;
            this.at = at;
            this.sword = sword;
        }

        private boolean advance() {
            double angle = frame * (0.72 + sword * 0.04);
            double radius = 0.28 + frame * 0.045;
            double y = at.y + frame * 0.12;
            for (int arm = 0; arm < 2; arm++) {
                double a = angle + arm * Math.PI;
                level.sendParticles(FINISHER[sword],
                    at.x + Math.cos(a) * radius, y, at.z + Math.sin(a) * radius,
                    3, 0.07, 0.06, 0.07, 0.015);
            }
            if (frame == 11)
                level.sendParticles(FINISHER_SPARK[sword], at.x, y, at.z,
                    24, 0.6, 0.35, 0.6, 0.045);
            return ++frame >= 12;
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag data = event.getOriginal().getPersistentData();
        if (data.contains(SAVE)) player.getPersistentData().put(SAVE, data.getCompound(SAVE).copy());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE_FINISHERS.remove(event.getEntity().getUUID());
    }

    private SwordMastery() {}
}
