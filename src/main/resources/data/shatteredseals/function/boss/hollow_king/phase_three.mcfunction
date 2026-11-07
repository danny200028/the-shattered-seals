tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Hollow King enters final phase!","color":"dark_red","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:reverse_portal ~ ~1 ~ 0.9 0.8 0.9 0.06 72 force @a[distance=..48]
playsound minecraft:entity.wither.spawn hostile @a[distance=..48] ~ ~ ~ 0.9 0.65
title @a[distance=..48] actionbar {"text":"Hollow King | FINAL PHASE","color":"red","bold":true}
