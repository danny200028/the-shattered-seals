tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Cinder Warden enters phase two!","color":"red","bold":true}
