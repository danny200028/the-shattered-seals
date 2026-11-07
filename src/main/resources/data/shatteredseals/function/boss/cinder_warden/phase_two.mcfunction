tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion","ss_minion_cinder_warden"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion","ss_minion_cinder_warden"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Cinder Warden enters phase two!","color":"red","bold":true}
# Chapter-specific audiovisual warning; fired once when the phase tag changes.
particle minecraft:flame ~ ~1 ~ 0.9 0.8 0.9 0.06 32 force @a[distance=..48]
playsound minecraft:entity.blaze.shoot hostile @a[distance=..48] ~ ~ ~ 0.75 1.1
title @a[distance=..48] actionbar {"text":"Cinder Warden | PHASE II","color":"gold","bold":true}
