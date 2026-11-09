package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.AdventureContracts;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Runtime GameTests for pure v1.0.56 contract eligibility and command wiring.
 * These run in the real NeoForge server without mutating player saves.
 */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TrialsBountiesGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void trialsRequireCompletedStoryAndNgPlus(GameTestHelper helper) {
        if (AdventureContracts.trialUnlocked(59, 1, 1, 1)
                || AdventureContracts.trialUnlocked(60, 0, 1, 1)
                || AdventureContracts.trialUnlocked(60, 1, 0, 1)
                || AdventureContracts.trialUnlocked(60, 1, 1, 0)
                || !AdventureContracts.trialUnlocked(60, 1, 1, 1)
                || !AdventureContracts.trialUnlocked(60, 1, 3, 6)) {
            helper.fail("Trial eligibility permits bypassing NG+ chapter progression");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void secondModifierRequiresLaterNgPlus(GameTestHelper helper) {
        if (!AdventureContracts.validTrialMask(1, 1)
                || !AdventureContracts.validTrialMask(1, 2)
                || AdventureContracts.validTrialMask(1, 3)
                || AdventureContracts.validTrialMask(2, 3)
                || !AdventureContracts.validTrialMask(3, 3)
                || AdventureContracts.validTrialMask(4, 7)) {
            helper.fail("Trial modifier gating is incorrect");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void bountyGoalsAreFinite(GameTestHelper helper) {
        for (int chapter = 1; chapter <= 6; chapter++) {
            int hunt = AdventureContracts.required(1, chapter);
            if (hunt < 4 || hunt > 7 || AdventureContracts.required(2, chapter) != 1
                    || AdventureContracts.required(3, chapter) != 1
                    || AdventureContracts.huntName(chapter).isBlank()) {
                helper.fail("Invalid bounty goals for chapter " + chapter);
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void playerContractCommandsRegistered(GameTestHelper helper) {
        var root = helper.getLevel().getServer().getCommands().getDispatcher().getRoot();
        var ss = root.getChild("shatteredseals");
        if (ss == null || ss.getChild("trial") == null || ss.getChild("bounty") == null) {
            helper.fail("v1.0.56 commands were not registered on the game server");
            return;
        }
        helper.succeed();
    }

    private TrialsBountiesGameTests() {}
}
