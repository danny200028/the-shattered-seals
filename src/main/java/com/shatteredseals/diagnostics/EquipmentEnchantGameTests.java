package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.enchantments.EquipmentEnchantStacks;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Runs the real equipment-combination maths in the GameTest server. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EquipmentEnchantGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void dualWieldedLevelsCombineAndCapAtFive(GameTestHelper helper) {
        if (EquipmentEnchantStacks.effective(1, 1) != 2
                || EquipmentEnchantStacks.effective(3, 3) != 5
                || EquipmentEnchantStacks.effective(0, 3) != 3
                || EquipmentEnchantStacks.effective(0, 0) != 0
                || EquipmentEnchantStacks.effective(-1, 1) != 1) {
            helper.fail("Combined custom weapon enchant ranks are incorrect");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void offhandDamageNeverDuplicatesMainhand(GameTestHelper helper) {
        double alone = EquipmentEnchantStacks.offhandContribution(3, 0, 0.7, 0.4);
        double both = EquipmentEnchantStacks.offhandContribution(1, 1, 0.7, 0.4);
        double capped = EquipmentEnchantStacks.offhandContribution(3, 3, 0.7, 0.4);
        if (alone != 0 || Math.abs(both - 0.4) > 0.00001
                || Math.abs(capped - 0.8) > 0.00001) {
            helper.fail("Dual-wield bonus does not respect effective rank cap");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void dualProcRollsRemainSingleAndBounded(GameTestHelper helper) {
        float baseline = EquipmentEnchantStacks.procChance(.13f, 3, 0);
        float dual = EquipmentEnchantStacks.procChance(.13f, 3, 3);
        if (Math.abs(baseline - .13f) > .0001f
                || Math.abs(dual - .19f) > .0001f
                || EquipmentEnchantStacks.procChance(.13f, 0, 0) != 0f) {
            helper.fail("Offhand proc chance scaling changed solo enchant behaviour");
            return;
        }
        helper.succeed();
    }

    private EquipmentEnchantGameTests() {}
}
