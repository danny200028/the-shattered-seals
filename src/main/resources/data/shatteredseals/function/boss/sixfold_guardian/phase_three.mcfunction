tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:vindicator ~ ~ ~ {Tags:["ss_minion","ss_minion_sixfold_guardian"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Sixfold Guardian enters final phase!","color":"yellow","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:totem_of_undying ~ ~1 ~ 0.9 0.8 0.9 0.06 72 force @a[distance=..48]
playsound minecraft:block.beacon.activate hostile @a[distance=..48] ~ ~ ~ 0.9 0.65
title @a[distance=..48] actionbar {"text":"Sixfold Guardian | FINAL PHASE","color":"red","bold":true}
