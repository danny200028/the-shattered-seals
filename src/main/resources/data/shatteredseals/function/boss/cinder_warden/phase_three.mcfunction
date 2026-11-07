tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Cinder Warden enters final phase!","color":"red","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:flame ~ ~1 ~ 0.9 0.8 0.9 0.06 72 force @a[distance=..48]
playsound minecraft:entity.blaze.shoot hostile @a[distance=..48] ~ ~ ~ 0.9 0.65
title @a[distance=..48] actionbar {"text":"Cinder Warden | FINAL PHASE","color":"red","bold":true}
