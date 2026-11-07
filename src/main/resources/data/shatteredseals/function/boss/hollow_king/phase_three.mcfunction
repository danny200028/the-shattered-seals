tag @s add ss_phase3
effect give @s minecraft:speed 99999 0 true
effect give @s minecraft:resistance 99999 0 true
execute positioned ~2 ~ ~2 run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Hollow King enters final phase!","color":"dark_red","bold":true}
