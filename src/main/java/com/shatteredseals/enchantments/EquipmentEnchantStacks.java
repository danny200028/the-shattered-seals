package com.shatteredseals.enchantments;

import com.shatteredseals.ShatteredSeals;
import java.util.Locale;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Real equipped-gear stacking. Item enchantments retain their individual levels;
 * only the effective result across worn armour or two equipped melee weapons is
 * aggregated, to a maximum power rank of V. No save migration or new item IDs.
 *
 * Armor: additive max health and shared class potion effect resolution.
 * Swords/axes: the off-hand contributes to attacks made using the main hand.
 * Proc effects execute once per enchantment type per successful melee hit, not
 * once per weapon, and do not bypass boss/sword cooldowns.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class EquipmentEnchantStacks {
    private static final ResourceLocation HEALTH_ID =
        ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "stacked_equipment_max_health");

    /** Extract a custom enchantment's rank from actual item registry holders. */
    public static int rank(ItemStack item, String id) {
        if (item.isEmpty()) return 0;
        ItemEnchantments enchants = item.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchants.entrySet()) {
            if (entry.getKey().unwrapKey().map(key ->
                    key.location().getNamespace().equals(ShatteredSeals.MOD_ID)
                        && key.location().getPath().equals(id)).orElse(false))
                return entry.getIntValue();
        }
        return 0;
    }

    private static boolean melee(ItemStack item) {
        return !item.isEmpty() && (item.is(ItemTags.SWORDS) || item.is(ItemTags.AXES));
    }

    /** 0 means absent. Same enchant on both hands sums ranks, max V. */
    public static int effective(int first, int second) {
        return Math.min(5, Math.max(0, first) + Math.max(0, second));
    }

    public static double linear(int level, double base, double step) {
        return level <= 0 ? 0 : base + (level - 1) * step;
    }

    /** The native main-hand attack enchant already handles its own damage. */
    public static double offhandContribution(int main, int off, double base, double step) {
        return off <= 0 ? 0 : Math.max(0,
            linear(effective(main, off), base, step) - linear(main, base, step));
    }

    private static int main(ServerPlayer p, String id) {
        return rank(p.getMainHandItem(), id);
    }

    private static int off(ServerPlayer p, String id) {
        return rank(p.getOffhandItem(), id);
    }

    private static int total(ServerPlayer p, String id) {
        return effective(main(p, id), off(p, id));
    }

    private static void score(ServerPlayer player, String objectiveName, int value) {
        var board = player.getScoreboard();
        var obj = board.getObjective(objectiveName);
        if (obj != null) board.getOrCreatePlayerScore(player, obj).set(value);
    }

    /** Worn equipment, NOT inventory contents, contributes persistent buffs. */
    @SubscribeEvent
    public static void onEquipmentTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;
        int angel = 0, phoenix = 0, phoenixPieces = 0;
        double maxHealth = 0;
        for (ItemStack worn : player.getArmorSlots()) {
            int a = rank(worn, "guardian_angel"), f = rank(worn, "phoenix_skin");
            angel += a;
            phoenix += f;
            if (f > 0) phoenixPieces++;
            maxHealth += linear(rank(worn, "lifebloom"), 1.0, 1.0);
            maxHealth += linear(rank(worn, "immortal_spirit"), 2.0, 1.5);
        }
        score(player, "ss_ench_abs", Math.min(5, angel));
        score(player, "ss_ench_fire", Math.min(5, phoenix));
        score(player, "ss_ench_firepieces", phoenixPieces);
        AttributeInstance max = player.getAttribute(Attributes.MAX_HEALTH);
        if (max == null) return;
        AttributeModifier existing = max.getModifier(HEALTH_ID);
        // Don't remove/reapply identical modifiers each second: briefly reducing
        // max health could otherwise clip full health during a reapplication.
        if (existing == null && maxHealth <= 0
                || existing != null && Math.abs(existing.amount() - maxHealth) < 0.00001) return;
        if (existing != null) max.removeModifier(HEALTH_ID);
        if (maxHealth > 0) max.addTransientModifier(
            new AttributeModifier(HEALTH_ID, maxHealth, AttributeModifier.Operation.ADD_VALUE));
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

    /** Add the second weapon's enchant damage before vanilla armour mitigation. */
    @SubscribeEvent
    public static void onMeleeIncoming(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getSource().getDirectEntity() != player
                || !(event.getEntity() instanceof Mob)
                || !melee(player.getMainHandItem()) || !melee(player.getOffhandItem())
                || event.getAmount() <= 0) return;
        double bonus = offhandContribution(main(player, "soul_edge"), off(player, "soul_edge"), 0.7, 0.4)
                     + offhandContribution(main(player, "keen_edge"), off(player, "keen_edge"), 0.5, 0.35)
                     + offhandContribution(main(player, "berserker"), off(player, "berserker"), 1.0, 0.65)
                     + offhandContribution(main(player, "kings_execution"), off(player, "kings_execution"), 1.5, 1.0);
        if (bonus > 0) event.setAmount(event.getAmount() + (float) bonus);
    }

    private static void buff(ServerPlayer p, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                             int seconds, int amplifier) {
        p.addEffect(new MobEffectInstance(effect, seconds * 20, amplifier, false, true));
    }

    private static void weaken(Mob mob, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                               int seconds, int amplifier) {
        if (mob.isAlive()) mob.addEffect(new MobEffectInstance(effect, seconds * 20, amplifier, false, true));
    }

    /** One proc roll per enchantment, not two separate weapon rolls. */
    public static float procChance(float base, int first, int second) {
        if (first == 0 && second == 0) return 0;
        // Keep the original base chance for one weapon, boost by 2 percentage
        // points per secondary rank (up to +6%) when truly dual-wielding.
        return Math.min(0.32f, base + (first > 0 && second > 0 ? Math.min(3, second) * 0.02f : 0.0f));
    }

    private static boolean proc(ServerPlayer p, String id, float base) {
        int m = main(p, id), o = off(p, id);
        return total(p, id) > 0 && p.getRandom().nextFloat() < procChance(base, m, o);
    }

    /**
     * Only a direct player melee strike qualifies. Secondary magic damage has
     * no player attacker and can't recursively create new enchantment procs.
     */
    @SubscribeEvent
    public static void onMeleeHit(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getSource().getDirectEntity() != player
                || !(event.getEntity() instanceof Mob target)
                || !melee(player.getMainHandItem())) return;
        // Main-hand enchantments work solo; offhand adds only if holding a second melee weapon.
        boolean dual = melee(player.getOffhandItem());
        int staggerMain = main(player, "stagger"), staggerOff = dual ? off(player, "stagger") : 0;
        double stagger = offhandContribution(staggerMain, staggerOff, 0.35, 0.2);
        if (stagger > 0 && target.isAlive())
            target.knockback(stagger, player.getX() - target.getX(), player.getZ() - target.getZ());
        if (!dual && !player.getOffhandItem().isEmpty()) {
            // For a shield/torch in offhand, never trigger a weapon-only enchant
            // from that second item. Mainhand enchantments still function.
        }
        trigger(player, target, dual);
    }

    private static int level(ServerPlayer player, String id, boolean dual) {
        return effective(main(player, id), dual ? off(player, id) : 0);
    }

    private static boolean activate(ServerPlayer p, String id, float base, boolean dual) {
        int m = main(p, id), o = dual ? off(p, id) : 0;
        return effective(m, o) > 0 && p.getRandom().nextFloat() < procChance(base, m, o);
    }

    private static void trigger(ServerPlayer p, Mob mob, boolean dual) {
        int v = level(p, "vampiric_edge", dual);
        if (v > 0 && activate(p, "vampiric_edge", 0.13f, dual))
            buff(p, MobEffects.REGENERATION, 2 + v - 1, v >= 4 ? 1 : 0);

        int soul = level(p, "soul_reaper", dual);
        if (soul > 0 && activate(p, "soul_reaper", 0.13f, dual))
            buff(p, MobEffects.REGENERATION, 3 + (int) Math.ceil((soul - 1) * 1.5), soul >= 4 ? 1 : 0);

        int fury = level(p, "dragons_fury", dual);
        if (fury > 0 && mob.isAlive() && activate(p, "dragons_fury", 0.16f, dual))
            mob.setRemainingFireTicks(Math.max(mob.getRemainingFireTicks(), (4 + 2 * (fury - 1)) * 20));

        int frost = level(p, "frostbite", dual);
        if (frost > 0 && activate(p, "frostbite", 0.16f, dual))
            weaken(mob, MobEffects.MOVEMENT_SLOWDOWN, 2 + frost - 1, frost >= 4 ? 1 : 0);

        int infernal = level(p, "infernal_wrath", dual);
        if (infernal > 0 && mob.isAlive() && activate(p, "infernal_wrath", 0.16f, dual))
            mob.setRemainingFireTicks(Math.max(mob.getRemainingFireTicks(), (3 + 2 * (infernal - 1)) * 20));

        if (activate(p, "celestial_judgement", 0.10f, dual) && mob.isAlive())
            mob.hurt(p.serverLevel().damageSources().magic(), 1.25f + p.getRandom().nextFloat() * 1.5f);

        if (activate(p, "thunderstrike", 0.10f, dual) && mob.isAlive())
            mob.hurt(p.serverLevel().damageSources().magic(), 0.7f + p.getRandom().nextFloat());

        // The original enchantment's player-facing proc chatter is intentionally
        // suppressed here to avoid chat spam on rapid classic 1.8-style hits.
    }

    /** Soul Collector grants extra VANILLA experience only for hostile kills. */
    @SubscribeEvent
    public static void onSoulCollectorKill(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob)
                || !(mob instanceof net.minecraft.world.entity.monster.Enemy)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !melee(player.getMainHandItem())
                || event.getSource().getDirectEntity() != player) return;
        int m = main(player, "soul_collector");
        int o = melee(player.getOffhandItem()) ? off(player, "soul_collector") : 0;
        int combined = effective(m, o);
        if (combined > 0) player.giveExperiencePoints(combined);
    }

    private EquipmentEnchantStacks() {}
}
