"""Skill XP from every hostile kill, dungeon-only milestones, save-safe migration."""
from pathlib import Path
import json
import unittest

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "src/main/resources/data/shatteredseals"
FN = DATA / "function"
JAVA = (ROOT / "src/main/java/com/shatteredseals/progression/DungeonKillProgress.java").read_text()
LOAD = (FN / "load.mcfunction").read_text()
TICK = (FN / "tick.mcfunction").read_text()
PTICK = (FN / "progression/tick.mcfunction").read_text()
INIT = (FN / "progression/init.mcfunction").read_text()
MIGRATE = (FN / "progression/migrate_dungeon_kills.mcfunction").read_text()
XP = (FN / "progression/kill_xp.mcfunction").read_text()
PASS = (FN / "progression/passives.mcfunction").read_text()
NEW_MIGRATION = (FN / "progression/migrate_hostile_xp.mcfunction").read_text()
SNAPSHOT = (ROOT / "src/main/java/com/shatteredseals/net/SkillSnapshot.java").read_text()

class SplitKillProgressTests(unittest.TestCase):
    def test_each_hostile_grants_character_xp_but_only_dungeons_count_for_milestones(self):
        self.assertIn('increment(player, "ss_hkills")', JAVA)
        self.assertIn('if (isEligibleDungeonEnemy(victim)) increment(player, "ss_dkills")', JAVA)
        self.assertIn('return victim instanceof Enemy', JAVA)
        self.assertIn('getSource().getEntity() instanceof ServerPlayer', JAVA)
        self.assertIn('ss_delta = @s ss_hkills', XP)
        self.assertIn('ss_delta -= @s ss_hprev', XP)
        self.assertIn('ss_hprev = @s ss_hkills', XP)
        self.assertIn('function shatteredseals:progression/award_kills', XP)
        self.assertNotIn('ss_dkills', XP.splitlines()[-4:])
        for objective in ('ss_hkills', 'ss_hprev', 'ss_hkill_migrated', 'ss_dkills'):
            self.assertIn(f'scoreboard objectives add {objective} dummy', LOAD)
        self.assertIn('ss_dkills=10..', TICK)
        self.assertIn('ss_dkills=40..', TICK)
        self.assertIn('ss_dkills=100..', TICK)
        self.assertNotIn('ss_hkills=', TICK)
        self.assertIn('ss_dkills matches 40..', PASS)
        self.assertNotIn('ss_hkills matches', PASS)

    def test_dungeon_tags_and_old_spawner_fallback_preserved(self):
        for marker in ('ss_dungeon_enemy', 'ss_boss', 'ss_minion', 'ss_miniboss',
                       'ss_depths_guardian', 'ss_secret_boss', 'ss_elite_'):
            self.assertIn(marker, JAVA)
        self.assertIn('getStructureWithPieceAt(victim.blockPosition(), DUNGEONS)', JAVA)
        self.assertIn('if (!isEligibleHostileMob(victim)) return false;', JAVA)
        self.assertIn('mob.getType() != EntityType.PILLAGER', JAVA)
        self.assertIn('isInDungeon(level, mob.blockPosition(), "smugglers_den")', JAVA)
        source = (ROOT / "scripts/generate_dungeons.py").read_text()
        self.assertIn('entity_tags("ss_dungeon_enemy", f"ss_chapter_{chapter}")', source)
        tag = json.loads((DATA / "tags/worldgen/structure/dungeon_kill_locations.json").read_text())
        self.assertEqual(len(tag["values"]), 6)
        suite = (ROOT / "src/main/java/com/shatteredseals/diagnostics/HostileMobProgressGameTests.java").read_text()
        self.assertIn('isEligibleDungeonEnemy(zombie)', suite)
        self.assertIn('isEligibleDungeonEnemy(cow)', suite)

    def test_all_twelve_masteries_need_dungeon_kills(self):
        for n in range(1, 13):
            f = (FN / f"talents/master_{n}.mcfunction").read_text()
            self.assertIn('ss_dkills matches 100..', f)
            self.assertNotIn('ss_hkills', f)
            self.assertIn('100 dungeon enemy kills', f)
        screen = (ROOT / "src/main/java/com/shatteredseals/client/SkillTreeScreen.java").read_text()
        self.assertIn('100 dungeon kills', screen)
        self.assertIn('Dungeon kills ', screen)
        self.assertIn('data.hostileKills()', screen)
        self.assertIn('read(board, player, "ss_hkills")', SNAPSHOT)
        self.assertIn('value.hostileKills()', SNAPSHOT)
        self.assertIn('int hostileKills = buf.readVarInt()', SNAPSHOT)
        self.assertIn('100 dungeon kills', (FN / "progression/status.mcfunction").read_text())

    def test_old_world_migration_runs_once_and_preserves_everything(self):
        self.assertIn('unless score @s ss_dkill_migrated matches 1.. run function shatteredseals:progression/migrate_dungeon_kills', PTICK)
        self.assertIn('unless score @s ss_hkill_migrated matches 1.. run function shatteredseals:progression/migrate_hostile_xp', PTICK)
        self.assertLess(PTICK.index('migrate_dungeon_kills'), PTICK.index('migrate_hostile_xp'))
        self.assertLess(PTICK.index('migrate_hostile_xp'), PTICK.index('progression/kill_xp'))
        self.assertIn('scoreboard players operation @s ss_hkills = @s ss_dkills', NEW_MIGRATION)
        self.assertIn('scoreboard players operation @s ss_hprev = @s ss_hkills', NEW_MIGRATION)
        self.assertIn('scoreboard players set @s ss_hkill_migrated 1', NEW_MIGRATION)
        for old in ('ss_level', 'ss_points', 'ss_spent', 'ss_xp', 'ss_mxp', 'ss_dkills'):
            self.assertNotIn(f'scoreboard players set @s {old} 0', NEW_MIGRATION)
        for obj in ('ss_hkills', 'ss_hprev', 'ss_hkill_migrated'):
            self.assertIn(f'scoreboard players set @s {obj} ', INIT)
        self.assertIn('scoreboard players set @s ss_dkill_migrated 1', INIT)
        self.assertNotIn('ss_hkills', (FN / "story/locate.mcfunction").read_text())
        self.assertIn('scoreboard players set @s ss_dkills 0', MIGRATE)

if __name__ == '__main__':
    unittest.main()
