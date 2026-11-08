package com.shatteredseals.combat;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Six server-authoritative, rare and tier-scaled sword passives.
 * Only successful direct melee hits roll. Cooldowns are per player AND
 * sword identity, persisted in player NBT, and cannot be bypassed by swapping.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class LegendarySwordPassives {
    private record Power(String id, String name, float chance, int cooldownTicks) {}
    private static final Power[] POWERS = {
        new Power("emberbound", "Inferno Slash", 0.05F, 900),
        new Power("kings_fang", "Executioner's Mark", 0.06F, 800),
        new Power("wardens_oath", "Guardian's Wrath", 0.05F, 1000),
        new Power("starfall", "Celestial Strike", 0.04F, 1100),
        new Power("gravekeeper", "Soul Harvest", 0.06F, 900),
        new Power("kings_bane", "Shattered Reality", 0.03F, 1200)
    };

    @SubscribeEvent
    public static void onMeleeDamage(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0 || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getSource().getDirectEntity() != player || !(event.getEntity() instanceof Mob target)) return;
        ItemStack held = player.getMainHandItem();
        if (!held.is(ItemTags.SWORDS)) return;
        CustomData custom = held.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return;
        CompoundTag tag = custom.copyTag();
        String id = tag.getString("ss_weapon");
        int tier = switch (tag.getString("ss_tier")) {
            case "dormant" -> 1;
            case "awakened" -> 2;
            case "ascended" -> 3;
            default -> 0;
        };
        if (tier == 0) return;
        for (Power power : POWERS) {
            if (!power.id().equals(id)) continue;
            ServerLevel level = player.serverLevel();
            long now = level.getGameTime();
            String cooldown = "SSPassiveCooldown_" + id;
            if (player.getPersistentData().getLong(cooldown) > now) return;
            if (player.getRandom().nextFloat() >= power.chance()) return;
            // Reserve the cooldown BEFORE applying secondary damage, to prevent recursion.
            player.getPersistentData().putLong(cooldown, now + power.cooldownTicks());
            trigger(player, target, power.id(), tier);
            player.getPersistentData().putBoolean("SSPassiveReadyPending_" + id, true);
            player.displayClientMessage(Component.literal(
                power.name() + " activated! Cooldown started: " + power.cooldownTicks() / 20
                + " seconds.").withStyle(ChatFormatting.GOLD), false);
            level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
                net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.8F + tier * 0.12F);
            return;
        }
    }

    /**
     * Notifies the player once when their saved sword cooldown finishes,
     * even if the sword was put away before the timer expired.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;
        CompoundTag persisted = player.getPersistentData();
        long now = player.serverLevel().getGameTime();
        for (Power power : POWERS) {
            String pendingKey = "SSPassiveReadyPending_" + power.id();
            if (!persisted.getBoolean(pendingKey)) continue;
            if (persisted.getLong("SSPassiveCooldown_" + power.id()) > now) continue;
            persisted.remove(pendingKey);
            player.displayClientMessage(Component.literal(
                power.name() + " is available again!").withStyle(ChatFormatting.GREEN), false);
        }
    }

    private static void trigger(ServerPlayer player, Mob target, String id, int tier) {
        ServerLevel level = player.serverLevel();
        switch (id) {
            case "emberbound" -> {
                for (Mob enemy : nearby(level, target, 2.0 + tier * 0.8)) {
                    enemy.setRemainingFireTicks(Math.max(enemy.getRemainingFireTicks(), tier * 60));
                    if (tier >= 3) buff(enemy, MobEffects.WEAKNESS, 80, 0);
                }
                level.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 1, target.getZ(),
                    15 + tier * 15, 0.7, 0.6, 0.7, 0.05);
            }
            case "kings_fang" -> {
                buff(target, MobEffects.WEAKNESS, 80 + tier * 40, tier >= 2 ? 1 : 0);
                buff(target, MobEffects.GLOWING, 100 + tier * 40, 0);
                if (tier == 3) buff(target, MobEffects.WITHER, 80, 0);
                level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(),
                    15 + tier * 8, 0.4, 0.7, 0.4, 0.1);
            }
            case "wardens_oath" -> {
                buff(player, MobEffects.DAMAGE_RESISTANCE, 60 + tier * 40, tier >= 3 ? 1 : 0);
                if (tier >= 2) buff(player, MobEffects.ABSORPTION, 100 + tier * 40, tier >= 3 ? 1 : 0);
                for (Mob enemy : nearby(level, player, 2.0 + tier * 0.8))
                    enemy.knockback(0.6 + tier * 0.2, player.getX() - enemy.getX(), player.getZ() - enemy.getZ());
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(),
                    18 + tier * 12, 1, 0.6, 1, 0.1);
            }
            case "starfall" -> {
                buff(target, MobEffects.GLOWING, 80 + tier * 40, 0);
                for (Mob enemy : nearby(level, target, 1.5 + tier)) {
                    if (enemy != target) enemy.hurt(level.damageSources().magic(), tier * 1.5F);
                    if (tier >= 2) buff(enemy, MobEffects.MOVEMENT_SLOWDOWN, 40 + tier * 20, 0);
                }
                level.sendParticles(ParticleTypes.END_ROD, target.getX(), target.getY() + 2.5, target.getZ(),
                    20 + tier * 15, 0.8, 1.2, 0.8, 0.07);
            }
            case "gravekeeper" -> {
                player.heal(tier * 1.75F);
                buff(target, MobEffects.WITHER, 40 + tier * 20, 0);
                if (tier == 3) buff(player, MobEffects.REGENERATION, 100, 0);
                level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(),
                    12 + tier * 10, 0.6, 0.6, 0.6, 0.04);
            }
            case "kings_bane" -> {
                for (Mob enemy : nearby(level, target, 1.5 + tier * 1.1)) {
                    if (enemy != target) enemy.hurt(level.damageSources().magic(), tier * 2.0F);
                    buff(enemy, MobEffects.WEAKNESS, 60 + tier * 30, tier == 3 ? 1 : 0);
                    if (tier >= 2) buff(enemy, MobEffects.MOVEMENT_SLOWDOWN, 50 + tier * 20, 0);
                }
                level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1.2, target.getZ(),
                    25 + tier * 20, 1.3, 0.9, 1.3, 0.08);
            }
        }
    }

    private static java.util.List<Mob> nearby(ServerLevel level, LivingEntity origin, double radius) {
        return level.getEntitiesOfClass(Mob.class, origin.getBoundingBox().inflate(radius),
            entity -> entity.isAlive() && !entity.isAlliedTo(origin));
    }

    private static void buff(LivingEntity entity, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                             int ticks, int amplifier) {
        entity.addEffect(new MobEffectInstance(effect, ticks, amplifier, false, true));
    }
    private LegendarySwordPassives() {}
}
