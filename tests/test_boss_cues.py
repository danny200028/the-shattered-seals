"""Boss phase telemetry: ensure every chapter warns players before new attacks."""
import unittest
from pathlib import Path

FUNCTIONS = Path(__file__).resolve().parents[1] / "src/main/resources/data/shatteredseals/function/boss"
BOSSES = ("smuggler_king","cinder_warden","hollow_knight","arcane_seer","sixfold_guardian","hollow_king")

class BossCueTests(unittest.TestCase):
    def test_unique_phase_warnings_for_all_six_bosses(self):
        particles=[]
        sounds=[]
        for boss in BOSSES:
            for stage in ("phase_two", "phase_three"):
                with self.subTest(boss=boss,stage=stage):
                    source=(FUNCTIONS/boss/f"{stage}.mcfunction").read_text()
                    self.assertEqual(source.count("particle minecraft:"), 1)
                    self.assertEqual(source.count("playsound minecraft:"), 1)
                    self.assertEqual(source.count("title @a[distance=..48] actionbar"), 1)
                    self.assertIn("tag @s add ss_phase",source)
                    self.assertIn(boss.replace("_"," ").title(),source)
                    particles.append(next(x.split()[1] for x in source.splitlines() if x.startswith("particle ")))
                    sounds.append(next(x.split()[1] for x in source.splitlines() if x.startswith("playsound ")))
        self.assertEqual(len(set(particles)),len(BOSSES))
        self.assertEqual(len(set(sounds)),len(BOSSES))

if __name__ == "__main__":
    unittest.main()
