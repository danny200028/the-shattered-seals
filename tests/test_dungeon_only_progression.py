"""Existing-world migration and hostile-only combat progression contracts."""
from pathlib import Path
import json, unittest

ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
FN=DATA/"function"
JAVA=(ROOT/"src/main/java/com/shatteredseals/progression/DungeonKillProgress.java").read_text()
LOAD=(FN/"load.mcfunction").read_text()
TICK=(FN/"tick.mcfunction").read_text()
PTICK=(FN/"progression/tick.mcfunction").read_text()
INIT=(FN/"progression/init.mcfunction").read_text()
MIGRATE=(FN/"progression/migrate_dungeon_kills.mcfunction").read_text()
XP=(FN/"progression/kill_xp.mcfunction").read_text()
PASS=(FN/"progression/passives.mcfunction").read_text()

class HostileKillOnlyTests(unittest.TestCase):
    def test_legacy_vanilla_mob_stat_does_not_grant_progression(self):
        self.assertIn("scoreboard objectives add ss_dkills dummy", LOAD)
        self.assertIn("scoreboard objectives add ss_dkill_migrated dummy", LOAD)
        self.assertIn("scoreboard objectives add ss_kills minecraft.custom:minecraft.mob_kills", LOAD)
        self.assertIn("ss_delta = @s ss_dkills", XP)
        self.assertNotIn("ss_kills", XP)
        self.assertIn("ss_dkills=10..", TICK)
        self.assertIn("ss_dkills=40..", TICK)
        self.assertIn("ss_dkills=100..", TICK)
        self.assertNotIn("ss_kills=", TICK)
        self.assertNotIn("ss_kills", PASS)
        self.assertIn('read(board, player, "ss_dkills")',
                      (ROOT/"src/main/java/com/shatteredseals/net/SkillSnapshot.java").read_text())

    def test_every_mastery_is_gated_by_hostile_kills(self):
        for n in range(1,13):
            f=(FN/f"talents/master_{n}.mcfunction").read_text()
            self.assertIn("ss_dkills matches 100..", f)
            self.assertNotIn("ss_kills", f)
            self.assertIn("100 hostile mob kills", f)
        screen=(ROOT/"src/main/java/com/shatteredseals/client/SkillTreeScreen.java").read_text()
        self.assertIn("100 hostile kills", screen)
        self.assertIn("Hostile kills ", screen)

    def test_hostile_mobs_count_everywhere_but_animals_never_count(self):
        self.assertIn("instanceof Enemy", JAVA)
        self.assertIn("event.getSource().getEntity() instanceof ServerPlayer", JAVA)
        self.assertIn("instanceof Mob victim", JAVA)
        self.assertIn('getObjective("ss_dkills")', JAVA)
        self.assertIn("public static boolean isEligibleHostileMob(Mob victim)", JAVA)
        self.assertIn("return victim instanceof Enemy", JAVA)
        self.assertIn("!isEligibleHostileMob(victim)", JAVA)
        self.assertNotIn("getStructureWithPieceAt(victim.blockPosition(), DUNGEONS)", JAVA)
        self.assertIn("mob.getType() != EntityType.PILLAGER", JAVA)
        self.assertIn('isInDungeon(level, mob.blockPosition(), "smugglers_den")', JAVA)
        source=(ROOT/"scripts/generate_dungeons.py").read_text()
        self.assertIn('entity_tags("ss_dungeon_enemy", f"ss_chapter_{chapter}")', source)
        tag=json.loads((DATA/"tags/worldgen/structure/dungeon_kill_locations.json").read_text())
        self.assertEqual(len(tag["values"]), 6)
        self.assertTrue(all(s.startswith("shatteredseals:") for s in tag["values"]))

    def test_first_login_migration_runs_once_without_resetting_skills_or_world(self):
        self.assertIn("unless score @s ss_dkill_migrated matches 1.. run function shatteredseals:progression/migrate_dungeon_kills", PTICK)
        self.assertIn("scoreboard players set @s ss_dkill_migrated 1", MIGRATE)
        self.assertIn("scoreboard players set @s ss_dkill_migrated 1", INIT)
        for objective in ("ss_dkills","ss_kprev","ss_rank"):
            self.assertIn(f"scoreboard players set @s {objective} 0", MIGRATE)
        for objective in ("ss_xp","ss_mxp"):
            self.assertIn(f"execute if score @s ss_kills matches 1.. run scoreboard players set @s {objective} 0", MIGRATE)
        for obj in ("ss_level","ss_points","ss_spent","ss_vk_smug","ss_ng","ss_t_van","ss_b11"):
            self.assertNotIn(f"scoreboard players set @s {obj} ", MIGRATE)
        self.assertNotIn("ss_dkills", (FN/"story/locate.mcfunction").read_text())
        # Previously counted dungeon mobs stay recorded, and the broadened
        # hostile-kill update does not perform a second save migration.
        self.assertNotIn("ss_dkills 0", PTICK)
        self.assertIn("ss_delta = @s ss_dkills", XP)
        self.assertIn("hostile mobs", JAVA.lower())
        self.assertIn("hostile mobs", (ROOT/"README.md").read_text().lower())

if __name__=="__main__": unittest.main()
