tag @s add ss_phase2
effect give @s minecraft:strength 99999 0 true
execute positioned ~2 ~ ~ run summon minecraft:pillager ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
execute positioned ~-2 ~ ~ run summon minecraft:pillager ~ ~ ~ {Tags:["ss_minion"],PersistenceRequired:0b}
tellraw @a[distance=..48] {"text":"Smuggler King enters phase two!","color":"gold","bold":true}
