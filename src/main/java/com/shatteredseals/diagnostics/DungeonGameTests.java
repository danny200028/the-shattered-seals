package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Real Minecraft headless tests: each test structure is actually placed
 * into a NeoForge GameTestServer world before its assertions run.
 *
 * Unlike the survival-world /shatteredseals audit, these tests intentionally
 * place large templates in a disposable, generated GameTest world.
 */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DungeonGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 200)
    public static void smugglersDen(GameTestHelper helper) {
        verify(helper, DungeonChapter.SMUGGLERS);
    }

    @GameTest(template = "cinder_citadel", timeoutTicks = 200)
    public static void cinderCitadel(GameTestHelper helper) {
        verify(helper, DungeonChapter.CINDER);
    }

    @GameTest(template = "hollow_crypt", timeoutTicks = 200)
    public static void hollowCrypt(GameTestHelper helper) {
        verify(helper, DungeonChapter.HOLLOW);
    }

    @GameTest(template = "arcane_observatory", timeoutTicks = 200)
    public static void arcaneObservatory(GameTestHelper helper) {
        verify(helper, DungeonChapter.ARCANE);
    }

    @GameTest(template = "sixfold_temple", timeoutTicks = 200)
    public static void sixfoldTemple(GameTestHelper helper) {
        verify(helper, DungeonChapter.SIXFOLD);
    }

    @GameTest(template = "hollow_kings_fortress", timeoutTicks = 200)
    public static void hollowKingsFortress(GameTestHelper helper) {
        verify(helper, DungeonChapter.KING);
    }

    private static void verify(GameTestHelper helper, DungeonChapter chapter) {
        var level = helper.getLevel();
        if (!level.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .containsKey(chapter.structure)) {
            helper.fail("Missing worldgen structure registry: " + chapter.structure);
            return;
        }
        // These are actual blocks loaded from the generated NBT into the
        // GameTest world, not a JSON/NBT text inspection.
        helper.assertBlockPresent(Blocks.CRYING_OBSIDIAN, new BlockPos(21, 0, 36));
        helper.assertBlockPresent(Blocks.CHEST, new BlockPos(21, 1, 38));
        helper.assertBlockPresent(Blocks.CHEST, new BlockPos(21, 1, 24));
        helper.assertBlockPresent(Blocks.SPAWNER, new BlockPos(7, 1, 7));
        helper.assertBlockPresent(Blocks.SPAWNER, new BlockPos(35, 1, 35));
        helper.assertBlockPresent(Blocks.GLOWSTONE, new BlockPos(21, 0, 21));

        String boss = chapter.boss.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
        if (!RuntimeAudit.replaySigilRolls(level, boss)) {
            helper.fail("Real loot roll did not grant chapter NG+ sigil: " + boss);
            return;
        }
        helper.succeed();
    }

    private DungeonGameTests() {}
}
