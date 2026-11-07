execute in minecraft:overworld run fill -5 79 -5 5 79 5 minecraft:polished_deepslate
execute in minecraft:overworld run fill -1 80 -1 1 82 1 minecraft:air
execute in minecraft:overworld run tp @s 0 80 0
tellraw @s {"text":"Returned to the Overworld origin on a safe landing pad.","color":"yellow","italic":false}
