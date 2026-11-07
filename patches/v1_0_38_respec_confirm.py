#!/usr/bin/env python3
"""Add explicit confirmation to existing Dragon Egg respec in extracted source."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
p=root/"src/main/resources/data/shatteredseals/function/progression/reset.mcfunction"
tick=root/"src/main/resources/data/shatteredseals/function/progression/tick.mcfunction"
confirm=root/"src/main/resources/data/shatteredseals/function/progression/reset_confirm.mcfunction"
apply=root/"src/main/resources/data/shatteredseals/function/progression/reset_apply.mcfunction"
for f in (p,tick,confirm,apply):
    if not f.is_file():
        raise SystemExit(f"Missing {f}; refusing patch")
if "execute if score @s ss_respec matches 1 run function shatteredseals:progression/reset_confirm" not in p.read_text():
    raise SystemExit("Unexpected reset implementation")
if "scoreboard players enable @a ss_respec" not in tick.read_text():
    raise SystemExit("Missing respec trigger enable")
if "execute store success score @s ss_egg run clear @s minecraft:dragon_egg 1" not in confirm.read_text():
    raise SystemExit("Missing atomic egg consumption")
p.write_text(
    "# v1.0.38: explicit two-step confirmation; only option 2 consumes the egg.\n"
    "execute if score @s ss_respec matches 1 run tellraw @s [{\"text\":\"Reset all skill-tree allocations for one Dragon Egg? \",\"color\":\"yellow\"},{\"text\":\"[CONFIRM RESET]\",\"color\":\"red\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/trigger ss_respec set 2\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Consume one Dragon Egg and refund spent skill points\"}}]\n"
    "execute if score @s ss_respec matches 2 run function shatteredseals:progression/reset_confirm\n"
    "execute if score @s ss_respec matches 3.. run tellraw @s {\"text\":\"Use /trigger ss_respec set 1 to review the reset first.\",\"color\":\"yellow\"}\n"
)
print("Respec confirmation patched; existing egg validation and refund logic preserved.")
