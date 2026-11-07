package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Runs the actual phase-two and phase-three datapack functions in Minecraft,
 * checks their boss-specific minions, then verifies that the cleanup command
 * removes only adds whose summoning boss is gone.
 *
 * Each scenario runs in the disposable GameTest world; no players or saves
 * are modified by these integration tests.
 */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BossPhaseGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 200)
    public static void smugglerPhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.SMUGGLERS, EntityType.PILLAGER);
    }

    @GameTest(template = "cinder_citadel", timeoutTicks = 200)
    public static void cinderPhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.CINDER, EntityType.HUSK);
    }

    @GameTest(template = "hollow_crypt", timeoutTicks = 200)
    public static void hollowPhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.HOLLOW, EntityType.WITHER_SKELETON);
    }

    @GameTest(template = "arcane_observatory", timeoutTicks = 200)
    public static void arcanePhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.ARCANE, EntityType.EVOKER);
    }

    @GameTest(template = "sixfold_temple", timeoutTicks = 200)
    public static void sixfoldPhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.SIXFOLD, EntityType.RAVAGER);
    }

    @GameTest(template = "hollow_kings_fortress", timeoutTicks = 200)
    public static void kingPhases(GameTestHelper helper) {
        verify(helper, DungeonChapter.KING, EntityType.RAVAGER);
    }

    private static void verify(GameTestHelper helper, DungeonChapter chapter,
                               EntityType<? extends Mob> bossType) {
        ServerLevel level = helper.getLevel();
        String bossName = chapter.boss.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
        Mob boss = bossType.create(level);
        if (boss == null) {
            helper.fail("Boss entity type was not created: " + bossName);
            return;
        }
        var pos = helper.absolutePos(new BlockPos(21, 2, 36));
        boss.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        boss.addTag("ss_boss");
        boss.addTag("ss_" + bossName);
        if (!level.addFreshEntity(boss)) {
            helper.fail("Boss entity was not added: " + bossName);
            return;
        }
        AABB arena = new AABB(pos).inflate(10.0);
        runAsBoss(level, boss, bossName, "phase_two");
        if (!boss.getTags().contains("ss_phase2")) {
            helper.fail("Phase II did not apply its boss tag: " + bossName);
            return;
        }
        int count = countMinions(level, arena, bossName);
        if (count != 2) {
            helper.fail("Phase II should summon two owned minions for " + bossName + ", got " + count);
            return;
        }
        runAsBoss(level, boss, bossName, "phase_three");
        if (!boss.getTags().contains("ss_phase3")) {
            helper.fail("Final phase did not apply its boss tag: " + bossName);
            return;
        }
        count = countMinions(level, arena, bossName);
        if (count != 3) {
            helper.fail("Final phase should have three owned minions for " + bossName + ", got " + count);
            return;
        }

        boss.discard();
        level.getServer().getCommands().performPrefixedCommand(
            level.getServer().createCommandSourceStack().withPermission(2),
            "function shatteredseals:boss/minion_cleanup");

        count = countMinions(level, arena, bossName);
        if (count != 0) {
            helper.fail("Owned minions survived cleanup for " + bossName + ": " + count);
            return;
        }
        helper.succeed();
    }

    private static int countMinions(ServerLevel level, AABB arena, String bossName) {
        return level.getEntitiesOfClass(Mob.class, arena,
            mob -> mob.isAlive() && mob.getTags().contains("ss_minion_" + bossName)).size();
    }

    private static void runAsBoss(ServerLevel level, Mob boss, String name, String stage) {
        level.getServer().getCommands().performPrefixedCommand(
            boss.createCommandSourceStack().withPermission(2),
            "function shatteredseals:boss/" + name + "/" + stage);
    }

    private BossPhaseGameTests() {}
}
