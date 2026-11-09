package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.FinalAdventure;
import com.shatteredseals.combat.SwordMastery;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Real generated structure placement and pure endgame state checks, not UI screenshots. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FinalWorldGameTests {
    @GameTest(template = "wardens_sanctuary", timeoutTicks = 400)
    public static void sanctuaryRoomsPlaced(GameTestHelper h) {
        h.assertBlockPresent(Blocks.LODESTONE, new BlockPos(40, 2, 24));
        h.assertBlockPresent(Blocks.BOOKSHELF, new BlockPos(65, 2, 3));
        h.assertBlockPresent(Blocks.DEEPSLATE_BRICKS, new BlockPos(0, 16, 0));
        h.assertBlockPresent(Blocks.GLASS, new BlockPos(55, 10, 40));
        h.assertBlockPresent(Blocks.ENCHANTING_TABLE, new BlockPos(38, 2, 8));
        h.assertBlockPresent(Blocks.FURNACE, new BlockPos(4, 2, 40));
        h.assertBlockPresent(Blocks.CHEST, new BlockPos(68, 2, 21));
        h.assertBlockPresent(Blocks.BREWING_STAND, new BlockPos(36, 2, 40));
        h.succeed();
    }
    @GameTest(template = "smugglers_tunnels_ruin", timeoutTicks = 150)
    public static void smugglersTunnels(GameTestHelper h) { checkRuin(h); }
    @GameTest(template = "emberfault_chasms_ruin", timeoutTicks = 150)
    public static void emberfaultChasms(GameTestHelper h) { checkRuin(h); }
    @GameTest(template = "drowned_ossuary_ruin", timeoutTicks = 150)
    public static void drownedOssuary(GameTestHelper h) { checkRuin(h); }
    @GameTest(template = "starshard_caverns_ruin", timeoutTicks = 150)
    public static void starshardCaverns(GameTestHelper h) { checkRuin(h); }
    @GameTest(template = "rootbound_grottos_ruin", timeoutTicks = 150)
    public static void rootboundGrottos(GameTestHelper h) { checkRuin(h); }
    @GameTest(template = "black_crown_depths_ruin", timeoutTicks = 150)
    public static void blackCrownDepths(GameTestHelper h) { checkRuin(h); }
    private static void checkRuin(GameTestHelper h) {
        h.assertBlockPresent(Blocks.CHEST, new BlockPos(15, 2, 10));
        h.assertBlockPresent(Blocks.CHISELED_DEEPSLATE, new BlockPos(10, 2, 10));
        h.succeed();
    }
    @GameTest(template = "smugglers_tunnels_vault", timeoutTicks = 150)
    public static void hiddenVaultHasRunesAndChest(GameTestHelper h) {
        h.assertBlockPresent(Blocks.CHEST, new BlockPos(6, 2, 10));
        h.assertBlockPresent(Blocks.IRON_BARS, new BlockPos(6, 2, 7));
        h.assertBlockPresent(Blocks.CHISELED_COPPER, new BlockPos(3, 2, 4));
        h.assertBlockPresent(Blocks.PRISMARINE, new BlockPos(6, 2, 4));
        h.succeed();
    }
    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void secretRequiresFullCycleAndMasteryRemainsCosmetic(GameTestHelper h) {
        if (FinalAdventure.secretUnlocked(63, 0)
                || FinalAdventure.secretUnlocked(62, 1)
                || !FinalAdventure.secretUnlocked(63, 1)
                || SwordMastery.rank(0) != 0 || SwordMastery.rank(8) != 1
                || SwordMastery.rank(20) != 2 || SwordMastery.rank(110) != 5
                || SwordMastery.rank(10000) != 5) {
            h.fail("Final progression gates or cosmetic sword mastery thresholds invalid");
            return;
        }
        h.succeed();
    }
    private FinalWorldGameTests() {}
}
