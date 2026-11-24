package com.shatteredseals.progression;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Wither Skull Hunt: vanilla skull loot (including Looting) stays unchanged.
 * After 20 player-killed Wither Skeletons without a skull, add one guaranteed
 * skull to the normal drops. A naturally dropped skull resets the streak.
 *
 * Progress is saved separately for each player through logout and respawn.
 * Only player-credited kills count, including ranged kills.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class WitherSkullPity {
    public static final int MAX_DRY_KILLS = 20;
    public static final String COUNTER_KEY = "SSWitherSkullDryKills";

    public record Result(int remainingDryKills, boolean addGuaranteedSkull) {}

    /** Pure state transition, suitable for actual in-game GameTest assertions. */
    public static Result next(int dryKills, boolean vanillaSkullDropped) {
        if (vanillaSkullDropped) return new Result(0, false);
        int count = Math.max(0, dryKills) + 1;
        return count >= MAX_DRY_KILLS
            ? new Result(0, true) : new Result(count, false);
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton victim)
                || !(victim.level() instanceof ServerLevel level)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        // Inspect ACTUAL drops after vanilla chance and Looting have been applied.
        boolean vanillaSkull = event.getDrops().stream()
            .anyMatch(drop -> drop.getItem().is(Items.WITHER_SKELETON_SKULL));

        CompoundTag saved = player.getPersistentData();
        Result result = next(saved.getInt(COUNTER_KEY), vanillaSkull);
        saved.putInt(COUNTER_KEY, result.remainingDryKills());

        if (result.addGuaranteedSkull()) {
            ItemEntity skull = new ItemEntity(level,
                victim.getX(), victim.getY() + 0.3D, victim.getZ(),
                new ItemStack(Items.WITHER_SKELETON_SKULL));
            skull.setDefaultPickUpDelay();
            event.getDrops().add(skull);
            player.displayClientMessage(Component.literal(
                "Wither Skull Hunt: guaranteed skull earned after "
                + MAX_DRY_KILLS + " skull-free kills!")
                .withStyle(ChatFormatting.GOLD), false);
        } else if (!vanillaSkull && (result.remainingDryKills() % 5 == 0
                || result.remainingDryKills() == MAX_DRY_KILLS - 1)) {
            player.displayClientMessage(Component.literal("Wither Skull Hunt: "
                + result.remainingDryKills() + "/" + MAX_DRY_KILLS
                + " skull-free kills").withStyle(ChatFormatting.GRAY), true);
        }
    }

    /** The player object is cloned on death; preserve this one saved counter. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag previous = event.getOriginal().getPersistentData();
        if (previous.contains(COUNTER_KEY)) {
            event.getEntity().getPersistentData().putInt(
                COUNTER_KEY, previous.getInt(COUNTER_KEY));
        }
    }

    private WitherSkullPity() {}
}
