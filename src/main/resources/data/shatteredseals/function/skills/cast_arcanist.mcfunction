effect give @s minecraft:night_vision 18 0 true
execute at @s run effect give @e[type=!minecraft:player,type=!minecraft:item,distance=1..8] minecraft:glowing 8 0 true
execute at @s run particle minecraft:enchant ~ ~1 ~ 1 1 1 0.2 50 force
scoreboard players set @s ss_cd 200
tellraw @s {"text":"Arcanist ability used! Cooldown: 10 seconds.","color":"light_purple","bold":false}
execute if score @s ss_rank matches 2.. run effect give @s minecraft:regeneration 8 0 true
execute if score @s ss_rank matches 3.. run effect give @s minecraft:absorption 12 1 true
execute if score @s ss_rank matches 3.. run scoreboard players set @s ss_cd 140




execute if score @s ss_a41 matches 1.. run function shatteredseals:talents/unique_10
execute if score @s ss_a42 matches 1.. run function shatteredseals:talents/unique_11
execute if score @s ss_a43 matches 1.. run function shatteredseals:talents/unique_12
