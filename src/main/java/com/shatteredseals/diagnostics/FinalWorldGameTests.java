package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.FinalAdventure;
import com.shatteredseals.adventure.ChronicleRewards;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Items;
import com.shatteredseals.combat.SwordMastery;
import com.shatteredseals.world.SanctuaryPlacement;
import com.shatteredseals.world.SanctuarySpawnWard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Real generated structure placement and pure endgame state checks, not UI screenshots. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FinalWorldGameTests {
    @GameTest(template = "wardens_sanctuary", timeoutTicks = 400)
    public static void sanctuaryRoomsPlaced(GameTestHelper h) {
        // Prove naturally generated hill / woodland material may be replaced,
        // but chests, crafted building stone and unbreakable blocks are protected.
        if (!SanctuaryPlacement.canClear(Blocks.GRASS_BLOCK.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.STONE.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.OAK_LOG.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.OAK_LEAVES.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.WATER.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.IRON_ORE.defaultBlockState())
                || !SanctuaryPlacement.canClear(Blocks.DEEPSLATE_COAL_ORE.defaultBlockState())
                || !SanctuarySpawnWard.insideWard(new BlockPos(40, 1, 24), new BlockPos(92, 1, 60))
                || SanctuarySpawnWard.insideWard(new BlockPos(40, 1, 24), new BlockPos(93, 1, 60))
                || SanctuaryPlacement.canClear(Blocks.CHEST.defaultBlockState())
                || SanctuaryPlacement.canClear(Blocks.STONE_BRICKS.defaultBlockState())
                || SanctuaryPlacement.canClear(Blocks.BEDROCK.defaultBlockState())) {
            h.fail("Natural terrain clearing rules or protected construction checks failed");
            return;
        }
        h.assertBlockPresent(Blocks.LODESTONE, new BlockPos(40, 2, 24));
        h.assertBlockPresent(Blocks.BOOKSHELF, new BlockPos(49, 24, 11));
        h.assertBlockPresent(Blocks.DEEPSLATE_BRICKS, new BlockPos(0, 14, 10));
        h.assertBlockPresent(Blocks.GLASS, new BlockPos(55, 23, 40));
        h.assertBlockPresent(Blocks.DEEPSLATE_TILES, new BlockPos(40, 40, 20));
        h.assertBlockPresent(Blocks.DEEPSLATE_BRICK_WALL, new BlockPos(8, 34, 4));
        assertStairFlight(h, 37, 0, 11, Direction.SOUTH);
        assertStairFlight(h, 42, 11, 21, Direction.NORTH);
        h.assertBlockPresent(Blocks.DARK_OAK_PLANKS, new BlockPos(38, 12, 22));
        h.assertBlockPresent(Blocks.DARK_OAK_PLANKS, new BlockPos(43, 12, 22));
        h.assertBlockPresent(Blocks.DEEPSLATE_TILES, new BlockPos(43, 23, 10));
        h.assertBlockPresent(Blocks.ENCHANTING_TABLE, new BlockPos(38, 13, 8));
        h.assertBlockPresent(Blocks.FURNACE, new BlockPos(4, 2, 40));
        h.assertBlockPresent(Blocks.CARTOGRAPHY_TABLE, new BlockPos(22, 24, 16));
        h.assertBlockPresent(Blocks.RED_BED, new BlockPos(37, 24, 24));
        h.assertBlockPresent(Blocks.CHEST, new BlockPos(68, 13, 21));
        h.assertBlockPresent(Blocks.BREWING_STAND, new BlockPos(36, 13, 42));
        h.succeed();
    }

    private static void assertStairFlight(GameTestHelper h, int left, int floor, int startZ, Direction uphill) {
        for (int x = left; x < left + 3; x++) {
            for (int rise = 1; rise <= 11; rise++) {
                BlockPos tread = new BlockPos(x, floor + rise + 1, startZ + (rise - 1) * uphill.getStepZ());
                h.assertBlockPresent(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, tread);
                var state = h.getBlockState(tread);
                if (state.getValue(StairBlock.FACING) != uphill
                        || state.getValue(StairBlock.HALF) != Half.BOTTOM
                        || state.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                    h.fail("Wrong stair geometry at " + tread);
                }
                for (int clearance = 1; clearance <= 3; clearance++) {
                    h.assertBlockPresent(Blocks.AIR, tread.above(clearance));
                }
            }
        }
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void readableChroniclesAndLegendaryCrown(GameTestHelper h) {
        for (int index = 0; index < 6; index++) {
            var book = ChronicleRewards.chronicleBook(index);
            var pages = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if (!book.is(Items.WRITTEN_BOOK) || pages == null || pages.pages().size() != 3
                    || !pages.title().raw().equals("Buried Chronicle " + (index + 1))) {
                h.fail("Chronicle " + index + " is not a three-page readable signed book");
                return;
            }
        }
        var epilogue = ChronicleRewards.epilogueBook();
        if (!epilogue.is(Items.WRITTEN_BOOK)
                || epilogue.get(DataComponents.WRITTEN_BOOK_CONTENT) == null
                || epilogue.get(DataComponents.WRITTEN_BOOK_CONTENT).pages().size() != 3) {
            h.fail("Chronicle epilogue is missing its three original story pages");
            return;
        }
        ArmorItem crown = ShatteredSeals.ARCHIVISTS_CROWN.get();
        if (crown.getType() != ArmorItem.Type.HELMET
                || crown.getDefense() != 4
                || crown.getToughness() != 3.0F
                || ChronicleRewards.ELITE_BONUS_XP != 100) {
            h.fail("Archivist's Crown is not functional legendary head armour");
            return;
        }
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
