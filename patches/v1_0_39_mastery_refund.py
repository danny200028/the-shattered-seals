#!/usr/bin/env python3
"""Refund purchased mastery upgrades on confirmed Dragon Egg respec."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
base=root/"src/main/resources/data/shatteredseals/function"
reset=base/"progression/reset_apply.mcfunction"
confirm=base/"progression/reset_confirm.mcfunction"
if not reset.is_file() or not confirm.is_file():
    raise SystemExit("Respec files missing")
original=reset.read_text()
if "scoreboard players operation @s ss_points += @s ss_spent" not in original:
    raise SystemExit("Unexpected existing respec implementation")
if "execute store success score @s ss_egg run clear @s minecraft:dragon_egg 1" not in confirm.read_text():
    raise SystemExit("Egg must be consumed only by confirmed respec")
# Verify each mastery purchase spends exactly one point and sets the corresponding allocation.
for i in range(1,13):
    class_id=(i-1)//3+1
    branch_id=(i-1)%3+1
    objective=f"ss_a{class_id}{branch_id}"
    f=base/f"talents/master_{i}.mcfunction"
    if not f.is_file():
        raise SystemExit(f"Missing {f}")
    content=f.read_text()
    # Follow the called function as some purchases delegate to a separate apply function.
    linked=[base/(m+".mcfunction") for m in re.findall(r"function shatteredseals:([a-z0-9_/]+)",content)]
    combined=content+"\n"+"\n".join(p.read_text() for p in linked if p.is_file())
    if not re.search(r"scoreboard players (?:remove @s ss_mpoints 1|add @s ss_mpoints -1)",combined):
        raise SystemExit(f"Unverified one-point mastery cost in {f}; refusing unsafe refund")
    if not re.search(rf"scoreboard players (?:set @s {objective} 1|add @s {objective} 1)",combined):
        raise SystemExit(f"Unverified allocation in {f}; refusing unsafe refund")
lines=["# Refund one mastery point for each purchased mastery allocation."]
for class_id in range(1,5):
    for branch_id in range(1,4):
        lines.append(f"execute if score @s ss_a{class_id}{branch_id} matches 1.. run scoreboard players add @s ss_mpoints 1")
reset.write_text("\n".join(lines)+"\n"+original.replace("Skill points refunded. One Dragon Egg consumed.","Skill and mastery points refunded. One Dragon Egg consumed."))
print("Mastery refund added, 12 purchases validated, original respec preserved.")
