"""Regression checks for passive stacking and reliable Night Vision refresh."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
FN = ROOT / "src/main/resources/data/shatteredseals/function"
PASS = (FN / "progression/passives.mcfunction").read_text()
MAST = (FN / "mastery/passives.mcfunction").read_text()
TICK = (FN / "tick.mcfunction").read_text()
PTICK = (FN / "progression/tick.mcfunction").read_text()
LOAD = (FN / "load.mcfunction").read_text()

class PassiveEffectTests(unittest.TestCase):
    def test_every_aggregator_scoreboard_objective_exists(self):
        for objective in ("ss_fx_str", "ss_fx_res", "ss_fx_spd", "ss_fx_haste",
                          "ss_fx_abs", "ss_fx_reg", "ss_fx_jump", "ss_fx_fire",
                          "ss_fx_night"):
            self.assertIn(f"scoreboard objectives add {objective} dummy", LOAD)
            self.assertIn(f"scoreboard players set @s {objective} 0", PASS)

    def test_two_haste_sources_sum_into_haste_two(self):
        self.assertIn("ss_t_arc matches 4.. run scoreboard players add @s ss_fx_haste 1", PASS)
        self.assertIn("ss_b42 matches 3.. run scoreboard players add @s ss_fx_haste 1", PASS)
        self.assertIn("ss_fx_haste matches 2 run effect give @s minecraft:haste 16 1 true", PASS)
        self.assertIn("ss_fx_haste += @s ss_mp_haste", MAST)
        self.assertIn("ss_fx_str += @s ss_mp_fury", MAST)
        self.assertIn("ss_fx_res += @s ss_mp_guard", MAST)
        self.assertIn("execute if score @s ss_ng matches 1.. run function shatteredseals:mastery/passives", PASS)
        self.assertNotIn("run function shatteredseals:mastery/passives", PTICK)

    def test_class_and_kill_contributions_do_not_double_apply(self):
        for cls, effect in ((1, "strength"), (2, "resistance"), (3, "speed")):
            self.assertRegex(PASS, rf"ss_path matches {cls} if score @s ss_kills matches 40\.\. run scoreboard players add")
            self.assertNotRegex(TICK, rf"ss_path={cls},ss_kills=.*minecraft:{effect}")
        for cls in range(1, 5):
            for line in PASS.splitlines():
                if "ss_b" in line and f"ss_path matches {cls}" in line:
                    self.assertTrue(line.startswith("execute if score @s ss_path"), line)

    def test_refresh_is_early_enough_to_prevent_night_vision_flicker(self):
        self.assertIn("execute if score #clock ss_clock matches 0 as @a run function shatteredseals:progression/passives", PTICK)
        self.assertIn("execute if score #clock ss_clock matches 80.. run scoreboard players set #clock ss_clock 0", TICK)
        self.assertIn("effect give @s minecraft:night_vision 30 0 true", PASS)
        self.assertNotRegex(TICK, r"effect give @s minecraft:night_vision 5")
        self.assertNotRegex(PASS, r"effect give @s minecraft:night_vision (?:1[0-4]|[0-9]) ")
        self.assertIn("ss_path matches 4 if score @s ss_kills matches 10.. run scoreboard players set @s ss_fx_night 1", PASS)

    def test_maximum_effect_level_is_five(self):
        for effect, objective in (("haste", "ss_fx_haste"), ("strength", "ss_fx_str"),
                                  ("resistance", "ss_fx_res"), ("speed", "ss_fx_spd")):
            self.assertIn(f"{objective} matches 5.. run effect give @s minecraft:{effect} 16 4 true", PASS)
            self.assertNotIn(f"minecraft:{effect} 16 5 true", PASS)

if __name__ == "__main__":
    unittest.main()
