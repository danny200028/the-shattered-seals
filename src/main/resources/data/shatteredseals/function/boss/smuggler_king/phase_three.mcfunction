tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:pillager ~ ~ ~ {Tags:["ss_minion","ss_minion_smuggler_king"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Smuggler King enters final phase!","color":"gold","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:smoke ~ ~1 ~ 0.9 0.8 0.9 0.06 72 force @a[distance=..48]
playsound minecraft:entity.pillager.celebrate hostile @a[distance=..48] ~ ~ ~ 0.9 0.65
title @a[distance=..48] actionbar {"text":"Smuggler King | FINAL PHASE","color":"red","bold":true}
