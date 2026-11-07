effect give @s minecraft:resistance 12 1 true
effect give @s minecraft:regeneration 8 1 true
particle minecraft:heart ~ ~1 ~ 1 1 1 0.3 30 force
scoreboard players set @s ss_cd 200
tellraw @s {"text":"Warden ability used! Cooldown: 10 seconds.","color":"blue","bold":false}
execute if score @s ss_rank matches 2.. run effect give @s minecraft:fire_resistance 12 0 true
execute if score @s ss_rank matches 3.. run effect give @s minecraft:absorption 14 2 true
execute if score @s ss_rank matches 3.. run scoreboard players set @s ss_cd 140




execute if score @s ss_a21 matches 1.. run function shatteredseals:talents/unique_4
execute if score @s ss_a22 matches 1.. run function shatteredseals:talents/unique_5
execute if score @s ss_a23 matches 1.. run function shatteredseals:talents/unique_6
