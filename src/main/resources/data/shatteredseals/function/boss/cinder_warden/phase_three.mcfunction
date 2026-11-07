tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:husk ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Cinder Warden enters final phase!","color":"red","bold":true}
