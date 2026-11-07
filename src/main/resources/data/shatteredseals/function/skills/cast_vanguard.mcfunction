effect give @s minecraft:strength 12 1 true
effect give @s minecraft:resistance 6 0 true
particle minecraft:crit ~ ~1 ~ 1 1 1 0.5 50 force
scoreboard players set @s ss_cd 200
tellraw @s {"text":"Vanguard ability used! Cooldown: 10 seconds.","color":"red","bold":false}
execute if score @s ss_rank matches 2.. run effect give @s minecraft:absorption 8 0 true
execute if score @s ss_rank matches 3.. run effect give @s minecraft:strength 8 2 true
execute if score @s ss_rank matches 3.. run scoreboard players set @s ss_cd 140




execute if score @s ss_a11 matches 1.. run function shatteredseals:talents/unique_1
execute if score @s ss_a12 matches 1.. run function shatteredseals:talents/unique_2
execute if score @s ss_a13 matches 1.. run function shatteredseals:talents/unique_3
