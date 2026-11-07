package com.shatteredseals.combat;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * A conservative approximation of 1.8.9 sword blocking. The client can only
 * signal its held-use intent. The server validates the sword, expiry, attack
 * direction and damage source on every hit.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class ClassicSwordGuard {
    private static final String GUARD_UNTIL = "ShatteredSealsSwordGuardUntil";
    private static final int HOLD_TIMEOUT = 8;
    private static final float DAMAGE_MULTIPLIER = 0.60F;

    public static void update(ServerPlayer player, boolean guarding) {
        if (!guarding || player.isSpectator() || !player.getMainHandItem().is(ItemTags.SWORDS)) {
            player.getPersistentData().remove(GUARD_UNTIL);
            return;
        }
        player.getPersistentData().putLong(GUARD_UNTIL, player.serverLevel().getGameTime() + HOLD_TIMEOUT);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isSpectator() || !player.getMainHandItem().is(ItemTags.SWORDS)) return;
        if (!player.getPersistentData().contains(GUARD_UNTIL)) return;
        if (player.serverLevel().getGameTime() > player.getPersistentData().getLong(GUARD_UNTIL)) return;
        Vec3 source = event.getSource().getSourcePosition();
        if (source == null) return; // Do not block fire, poison or ambient damage.
        Vec3 facing = player.getLookAngle();
        double dx = source.x - player.getX();
        double dz = source.z - player.getZ();
        double hitLength = Math.hypot(dx, dz);
        double facingLength = Math.hypot(facing.x, facing.z);
        if (hitLength < 0.01 || facingLength < 0.01) return;
        double forward = (dx * facing.x + dz * facing.z) / (hitLength * facingLength);
        if (forward < 0.35) return; // Attacks from the side/rear remain full strength.
        event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER);
    }

    private ClassicSwordGuard() {}
}
