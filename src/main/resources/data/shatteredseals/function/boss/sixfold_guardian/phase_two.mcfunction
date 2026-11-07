tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:vindicator ~ ~ ~ {Tags:["ss_minion","ss_minion_sixfold_guardian"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:vindicator ~ ~ ~ {Tags:["ss_minion","ss_minion_sixfold_guardian"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Sixfold Guardian enters phase two!","color":"yellow","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:totem_of_undying ~ ~1 ~ 0.9 0.8 0.9 0.06 32 force @a[distance=..48]
playsound minecraft:block.beacon.activate hostile @a[distance=..48] ~ ~ ~ 0.75 1.1
title @a[distance=..48] actionbar {"text":"Sixfold Guardian | PHASE II","color":"gold","bold":true}
