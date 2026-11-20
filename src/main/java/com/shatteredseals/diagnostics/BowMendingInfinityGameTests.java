package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Vanilla anvil compatibility comes from Enchantment.areCompatible and
 * EnchantmentHelper.isEnchantmentCompatible. The only relaxed vanilla conflict
 * should be the Infinity / Mending pair, while both enchantments keep their
 * original vanilla functionality and supported equipment.
 */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BowMendingInfinityGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void infinityAndMendingWorkOnOneBow(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess()
            .registryOrThrow(Registries.ENCHANTMENT);
        var infinity = registry.getHolderOrThrow(Enchantments.INFINITY);
        var mending = registry.getHolderOrThrow(Enchantments.MENDING);
        ItemStack bow = new ItemStack(Items.BOW);

        // Both orders must work through vanilla's anvil compatibility logic.
        if (!bow.supportsEnchantment(infinity) || !bow.supportsEnchantment(mending)
                || !Enchantment.areCompatible(infinity, mending)
                || !Enchantment.areCompatible(mending, infinity)
                || !EnchantmentHelper.isEnchantmentCompatible(List.of(infinity), mending)
                || !EnchantmentHelper.isEnchantmentCompatible(List.of(mending), infinity)) {
            helper.fail("Infinity and Mending must be mutually compatible on bows");
            return;
        }

        // Keeping both levels means neither enchantment is replaced or lost.
        EnchantmentHelper.updateEnchantments(bow, mutable -> {
            mutable.set(infinity, 1);
            mutable.set(mending, 1);
        });
        var applied = EnchantmentHelper.getEnchantmentsForCrafting(bow);
        if (applied.getLevel(infinity) != 1 || applied.getLevel(mending) != 1) {
            helper.fail("Bow must retain both Infinity I and Mending I");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void unrelatedVanillaConflictsRemainEnforced(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess()
            .registryOrThrow(Registries.ENCHANTMENT);
        var sharpness = registry.getHolderOrThrow(Enchantments.SHARPNESS);
        var smite = registry.getHolderOrThrow(Enchantments.SMITE);
        var fortune = registry.getHolderOrThrow(Enchantments.BLOCK_FORTUNE);
        var silkTouch = registry.getHolderOrThrow(Enchantments.SILK_TOUCH);
        if (Enchantment.areCompatible(sharpness, smite)
                || Enchantment.areCompatible(fortune, silkTouch)) {
            helper.fail("The bow change must not unlock unrelated incompatible enchantments");
            return;
        }
        helper.succeed();
    }

    private BowMendingInfinityGameTests() {}
}
