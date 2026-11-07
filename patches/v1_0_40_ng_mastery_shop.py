#!/usr/bin/env python3
"""NG+ mastery perk shop, passive powers and Dragon Egg refunds."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
base=root/"src/main/resources/data/shatteredseals/function"
tick=base/"progression/tick.mcfunction"
reset=base/"progression/reset_apply.mcfunction"
if not tick.is_file() or not reset.is_file():
    raise SystemExit("Required progression files missing")
# Find the scoreboard registration file rather than assuming a fixed datapack layout.
candidates=[p for p in base.rglob("*.mcfunction") if "scoreboard objectives add ss_mpoints " in p.read_text()]
if len(candidates)!=1:
    raise SystemExit(f"Expected one scoreboard registration file, got {candidates}")
load=candidates[0]
newobjs=["ss_mperk","ss_mp_guard","ss_mp_haste","ss_mp_fury"]
init=load.read_text()
for objective in newobjs:
    if f"scoreboard objectives add {objective} " in init:
        raise SystemExit(f"Already registered: {objective}")
    init+=f"\nscoreboard objectives add {objective} {'trigger' if objective=='ss_mperk' else 'dummy'}"
load.write_text(init+"\n")
ticktext=tick.read_text()
if "scoreboard players enable @a ss_ascend" not in ticktext:
    raise SystemExit("Unexpected tick implementation")
ticktext+="\n# NG+ mastery perk shop and 1-second passive refresh\n"
ticktext+="scoreboard players enable @a ss_mperk\n"
ticktext+="execute as @a[scores={ss_mperk=1..}] run function shatteredseals:mastery/shop\n"
ticktext+="scoreboard players set @a[scores={ss_mperk=1..}] ss_mperk 0\n"
ticktext+="execute if score #clock ss_clock matches 0 as @a[scores={ss_ng=1..}] run function shatteredseals:mastery/passives\n"
tick.write_text(ticktext)
perks={"guard":("resistance","Defender"),"haste":("haste","Swiftblade"),"fury":("strength","Warbringer")}
shop=["# /trigger ss_mperk set 9 to open the NG+ mastery shop"]
shop.append('execute if score @s ss_mperk matches 9 run tellraw @s {"text":"NEW GAME+ MASTERY | Spend earned mastery points (1 per rank, max 5).","color":"light_purple"}')
for index,(key,(_,label)) in enumerate(perks.items(),1):
    objective=f"ss_mp_{key}"
    shop.append('execute if score @s ss_mperk matches 9 run tellraw @s '+ '{"text":"['+label+'] Buy rank","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_mperk set '+str(index)+'"}}')
    shop.append(f"execute if score @s ss_mperk matches {index} unless score @s ss_ng matches 1.. run tellraw @s "+'{"text":"New Game+ required.","color":"red"}')
    shop.append(f"execute if score @s ss_mperk matches {index} if score @s ss_ng matches 1.. unless score @s ss_mpoints matches 1.. run tellraw @s "+'{"text":"Not enough mastery points.","color":"red"}')
    shop.append(f"execute if score @s ss_mperk matches {index} if score @s {objective} matches 5.. run tellraw @s "+'{"text":"This mastery perk is maxed.","color":"yellow"}')
    shop.append(f"execute if score @s ss_mperk matches {index} if score @s ss_ng matches 1.. if score @s ss_mpoints matches 1.. if score @s {objective} matches ..4 run function shatteredseals:mastery/buy_{key}")
    (base/f"mastery/buy_{key}.mcfunction").parent.mkdir(parents=True,exist_ok=True)
    (base/f"mastery/buy_{key}.mcfunction").write_text(
        f"scoreboard players remove @s ss_mpoints 1\nscoreboard players add @s {objective} 1\n"
        +f'tellraw @s {{"text":"{label} mastery upgraded!","color":"green"}}\n')
(base/"mastery/shop.mcfunction").write_text("\n".join(shop)+"\n")
passives=["# Refresh rank-scaled NG+ effects once per second"]
for key,(effect,_) in perks.items():
    for rank in range(1,6):
        passives.append(f"execute if score @s ss_mp_{key} matches {rank} run effect give @s minecraft:{effect} 2 {rank-1} true")
(base/"mastery/passives.mcfunction").write_text("\n".join(passives)+"\n")
resettext=reset.read_text()
if "scoreboard players operation @s ss_points += @s ss_spent" not in resettext:
    raise SystemExit("Expected existing reset refund ledger")
# Refund purchased NG+ mastery perks before clearing them.
refund=["# Refund all invested NG+ mastery points without changing mastery rank."]
for key in perks:
    refund.append(f"scoreboard players operation @s ss_mpoints += @s ss_mp_{key}")
    refund.append(f"scoreboard players set @s ss_mp_{key} 0")
reset.write_text("\n".join(refund)+"\n"+resettext)
print(f"NG+ mastery shop built; 3 perks, 15 ranks, reset refunds; objectives in {load}")
