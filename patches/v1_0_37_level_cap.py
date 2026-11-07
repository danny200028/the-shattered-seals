#!/usr/bin/env python3
"""Apply the v1.0.37 level-cap patch to an extracted v1.0.36 source tree."""
from pathlib import Path

root = Path(__file__).resolve().parents[1]
progression = root / "src/main/resources/data/shatteredseals/function/progression"
original = progression / "level_up.mcfunction"
if not original.is_file():
    raise SystemExit("Missing existing level_up.mcfunction; refusing to patch")
old = original.read_text()
expected = ("scoreboard players operation @s ss_xp -= @s ss_need",
            "scoreboard players add @s ss_level 1",
            "scoreboard players add @s ss_points 3",
            "function shatteredseals:progression/recalculate")
if not all(line in old for line in expected):
    raise SystemExit("Unexpected level-up implementation; refusing to patch")
if "level_up_apply" in old:
    raise SystemExit("Already patched; refusing to patch twice")
(progression / "level_up_apply.mcfunction").write_text(old)
original.write_text(
    "# v1.0.37: level 60 is the maximum normal character level.\n"
    "execute if score @s ss_level matches ..59 run function shatteredseals:progression/level_up_apply\n"
    "execute if score @s ss_level matches 60.. run function shatteredseals:progression/level_cap\n"
)
(progression / "level_cap.mcfunction").write_text(
    "# Clamp normal progression; preserve earned skill points and mastery rank.\n"
    "execute if score @s ss_level matches 61.. run scoreboard players set @s ss_level 60\n"
    "scoreboard players set @s ss_xp 0\n"
    "scoreboard players set @s ss_need 2147483647\n"
)
print("Applied v1.0.37 level-cap patch; original level-up rewards preserved.")
