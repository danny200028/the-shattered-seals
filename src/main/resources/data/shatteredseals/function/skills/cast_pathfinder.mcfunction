effect give @s minecraft:speed 15 2 true
effect give @s minecraft:jump_boost 15 1 true
effect give @s minecraft:slow_falling 10 0 true
scoreboard players set @s ss_cd 200
tellraw @s {"text":"Pathfinder ability used! Cooldown: 10 seconds.","color":"green","bold":false}
execute if score @s ss_rank matches 2.. run effect give @s minecraft:invisibility 5 0 true
execute if score @s ss_rank matches 3.. run effect give @s minecraft:haste 18 1 true
execute if score @s ss_rank matches 3.. run scoreboard players set @s ss_cd 140




execute if score @s ss_a31 matches 1.. run function shatteredseals:talents/unique_7
execute if score @s ss_a32 matches 1.. run function shatteredseals:talents/unique_8
execute if score @s ss_a33 matches 1.. run function shatteredseals:talents/unique_9
