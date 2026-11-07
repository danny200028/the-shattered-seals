tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Hollow Knight enters phase two!","color":"dark_purple","bold":true}
