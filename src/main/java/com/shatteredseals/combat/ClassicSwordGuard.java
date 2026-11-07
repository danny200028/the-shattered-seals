package com.shatteredseals.combat;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        if (player.serverLevel().getGameTime() > player.getPersistentData().getLong(GUARD_UNTIL)) {
            player.getPersistentData().remove(GUARD_UNTIL);
            return;
        }
        if (event.getAmount() <= 0.0F) return;
        // Some damage sources expose their attacker but no explicit position.
        Vec3 source = event.getSource().getSourcePosition();
        if (source == null && event.getSource().getEntity() != null)
            source = event.getSource().getEntity().position();
        if (source == null) return; // Ambient damage is never guardable.
        if (!isFacingAttack(player.position(), player.getLookAngle(), source)) return;
        event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER);
        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK,
            SoundSource.PLAYERS, 0.7F, 1.15F);
    }

    /** The guarded hit must arrive in the horizontal frontal cone. */
    public static boolean isFacingAttack(Vec3 playerPos, Vec3 look, Vec3 attackerPos) {
        double dx = attackerPos.x - playerPos.x;
        double dz = attackerPos.z - playerPos.z;
        double hitLength = Math.hypot(dx, dz);
        double facingLength = Math.hypot(look.x, look.z);
        if (hitLength < 0.01 || facingLength < 0.01) return false;
        double forward = (dx * look.x + dz * look.z) / (hitLength * facingLength);
        return forward >= 0.35;
    }

    private ClassicSwordGuard() {}
}
