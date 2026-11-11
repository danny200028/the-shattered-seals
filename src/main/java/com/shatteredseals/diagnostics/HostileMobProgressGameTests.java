package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.progression.DungeonKillProgress;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Runtime hostile/animal classification using actual Minecraft entity types. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HostileMobProgressGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void vanillaHostilesCountWithoutDungeonTags(GameTestHelper helper) {
        var level = helper.getLevel();
        Mob[] hostiles = {
            EntityType.ZOMBIE.create(level), EntityType.CREEPER.create(level),
            EntityType.SKELETON.create(level), EntityType.PILLAGER.create(level)
        };
        for (Mob hostile : hostiles) {
            if (hostile == null || !DungeonKillProgress.isEligibleHostileMob(hostile)) {
                helper.fail("Hostile vanilla mob excluded outside custom dungeon logic");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void passiveAnimalsDoNotCount(GameTestHelper helper) {
        var level = helper.getLevel();
        Mob[] animals = {
            EntityType.COW.create(level), EntityType.PIG.create(level),
            EntityType.CHICKEN.create(level), EntityType.SHEEP.create(level)
        };
        for (Mob animal : animals) {
            if (animal == null || DungeonKillProgress.isEligibleHostileMob(animal)) {
                helper.fail("Passive animal improperly counted as hostile RPG kill");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void onlyTaggedHostileEncountersQualifyForMilestones(GameTestHelper helper) {
        var world = helper.getLevel();
        Mob zombie = EntityType.ZOMBIE.create(world);
        Mob cow = EntityType.COW.create(world);
        if (zombie == null || cow == null) {
            helper.fail("Could not create mobs for milestone test");
            return;
        }
        zombie.addTag("ss_dungeon_enemy");
        cow.addTag("ss_dungeon_enemy");
        if (!DungeonKillProgress.isEligibleDungeonEnemy(zombie)
                || DungeonKillProgress.isEligibleDungeonEnemy(cow)) {
            helper.fail("Only hostile dungeon encounters should qualify for milestones");
            return;
        }
        helper.succeed();
    }

    private HostileMobProgressGameTests() {}
}
