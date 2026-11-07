"""Boss minions must never persist as roaming mobs after their boss is defeated."""
import unittest
from pathlib import Path

FUNCTIONS=Path(__file__).resolve().parents[1]/"src/main/resources/data/shatteredseals/function"
BOSSES=("smuggler_king","cinder_warden","hollow_knight","arcane_seer","sixfold_guardian","hollow_king")
class EncounterCleanupTests(unittest.TestCase):
    def test_owned_minions_in_both_boss_phases(self):
        cleanup=(FUNCTIONS/"boss/minion_cleanup.mcfunction").read_text()
        tick=(FUNCTIONS/"tick.mcfunction").read_text()
        self.assertIn("function shatteredseals:boss/minion_cleanup",tick)
        for boss in BOSSES:
            self.assertIn("tag=ss_minion_"+boss,cleanup)
            self.assertIn("tag=ss_"+boss+",distance=..96",cleanup)
            for phase in ("phase_two","phase_three"):
                with self.subTest(boss=boss,phase=phase):
                    phase_file=(FUNCTIONS/f"boss/{boss}/{phase}.mcfunction").read_text()
                    self.assertIn('Tags:["ss_minion","ss_minion_'+boss+'"]',phase_file)
                    self.assertNotIn('Tags:["ss_minion"]',phase_file)

    def test_legacy_orphans_cleanup_does_not_touch_new_adds(self):
        source=(FUNCTIONS/"boss/minion_cleanup.mcfunction").read_text()
        self.assertIn("tag=ss_minion,",source)
        for boss in BOSSES:
            self.assertIn("tag=!ss_minion_"+boss,source)
        self.assertIn("unless entity @e[tag=ss_boss,distance=..96] run kill @s",source)
if __name__=="__main__":
    unittest.main()
