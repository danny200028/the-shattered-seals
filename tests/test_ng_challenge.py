"""Endless cycles must escalate combat and mastery rewards without unbounded scaling."""
from pathlib import Path
import unittest

DATA=Path(__file__).resolve().parents[1]/"src/main/resources/data/shatteredseals/function"
BOSSES=("smuggler_king","cinder_warden","hollow_knight","arcane_seer","sixfold_guardian","hollow_king")

class NgChallengeTests(unittest.TestCase):
    def test_bosses_escalate_and_cap_on_cycle_four(self):
        for boss in BOSSES:
            content=(DATA/f"boss/summon_{boss}.mcfunction").read_text()
            with self.subTest(boss=boss):
                self.assertIn("ss_ng matches 1 run effect give",content)
                self.assertIn("ss_ng matches 2 run effect give",content)
                self.assertIn("ss_ng matches 3 run effect give",content)
                self.assertIn("ss_ng matches 4.. run effect give",content)
                for amp in range(2,6):
                    self.assertIn(f"minecraft:strength 99999 {amp} true",content)
                self.assertIn("ss_ng matches 4.. run effect give",content)
                self.assertIn("minecraft:resistance 99999 1 true",content)

    def test_mastery_xp_increases_but_caps(self):
        xp=(DATA/"progression/boss_xp.mcfunction").read_text()
        self.assertIn("ss_ng matches 1.. run scoreboard players add @s ss_mxp 150",xp)
        for cycle in (2,3,4):
            self.assertIn(f"ss_ng matches {cycle}.. run scoreboard players add @s ss_mxp 50",xp)
        self.assertNotIn("ss_ng matches 5..",xp)

if __name__=="__main__":
    unittest.main()
