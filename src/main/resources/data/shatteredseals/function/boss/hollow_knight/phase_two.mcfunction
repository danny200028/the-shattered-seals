tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion","ss_minion_hollow_knight"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion","ss_minion_hollow_knight"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Hollow Knight enters phase two!","color":"dark_purple","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:soul ~ ~1 ~ 0.9 0.8 0.9 0.06 32 force @a[distance=..48]
playsound minecraft:entity.wither_skeleton.hurt hostile @a[distance=..48] ~ ~ ~ 0.75 1.1
title @a[distance=..48] actionbar {"text":"Hollow Knight | PHASE II","color":"gold","bold":true}
