package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.enchantments.TableEnchantmentPreviewSender;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The preview must not secretly truncate the list to Minecraft's one-item clue.
 * Deterministic level-30 sword seeds must include both single and multi rolls.
 */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class VanillaEnchantmentPreviewGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void fullPreviewPreservesMultiEnchantRolls(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        boolean foundMultiple = false;
        boolean foundSingle = false;
        for (int seed = 1; seed <= 512; seed++) {
            var chosen = TableEnchantmentPreviewSender.selectVanillaEnchantments(
                helper.getLevel().registryAccess(), sword, seed, 2, 30);
            if (chosen.size() == 1) foundSingle = true;
            if (chosen.size() > 1) foundMultiple = true;
            if (foundSingle && foundMultiple) break;
        }
        if (!foundMultiple || !foundSingle) {
            helper.fail("Level-30 vanilla sword preview must expose both single- and multi-enchantment outcomes");
            return;
        }
        helper.succeed();
    }

    private VanillaEnchantmentPreviewGameTests() {}
}
