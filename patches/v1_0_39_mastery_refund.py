#!/usr/bin/env python3
"""Audit and preserve exact refunds for the existing twelve mastery allocations."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
base=root/"src/main/resources/data/shatteredseals/function"
reset=base/"progression/reset_apply.mcfunction"
confirm=base/"progression/reset_confirm.mcfunction"
if not reset.is_file() or not confirm.is_file():
    raise SystemExit("Respec files missing")
original=reset.read_text()
if "scoreboard players operation @s ss_points += @s ss_spent" not in original or "scoreboard players set @s ss_spent 0" not in original:
    raise SystemExit("Refund ledger missing; refusing to modify")
if "execute store success score @s ss_egg run clear @s minecraft:dragon_egg 1" not in confirm.read_text():
    raise SystemExit("Confirmed egg consumption missing")
for i in range(1,13):
    cls=(i-1)//3+1
    branch=(i-1)%3+1
    objective=f"ss_a{cls}{branch}"
    apply=base/f"talents/master_apply_{i}.mcfunction"
    purchase=base/f"talents/master_{i}.mcfunction"
    if not apply.is_file() or not purchase.is_file():
        raise SystemExit(f"Mastery {i} files missing")
    text=apply.read_text()
    for line in ("scoreboard players remove @s ss_points 8",
                 "scoreboard players add @s ss_spent 8",
                 f"scoreboard players set @s {objective} 1"):
        if line not in text:
            raise SystemExit(f"Mastery {i} accounting changed: {line}")
    if f"scoreboard players set @s {objective} 0" not in original:
        raise SystemExit(f"Mastery {i} not cleared by reset")
# Mastery purchases already contribute 8 skill points to ss_spent, which reset
# refunds. Adding another refund would duplicate currency. ss_mpoints is a
# separate NG+ pool and is not spent by these twelve mastery purchases.
reset.write_text(original.replace(
    "Skill points refunded. One Dragon Egg consumed.",
    "Skill points, including all purchased mastery unlocks, refunded. One Dragon Egg consumed."
))
print("Validated all 12 mastery unlocks: 8 normal points each already tracked in ss_spent. No double refund.")
