# Calculate ten-segment bar with integer scoreboard arithmetic
scoreboard players set @s ss_hudfill 0
execute if score @s ss_ng matches 0 run scoreboard players operation @s ss_hudtmp = @s ss_xp
execute if score @s ss_ng matches 1.. run scoreboard players operation @s ss_hudtmp = @s ss_mxp
scoreboard players set #ten ss_hudtmp 10
scoreboard players operation @s ss_hudtmp *= #ten ss_hudtmp
execute if score @s ss_ng matches 0 if score @s ss_need matches 1.. run scoreboard players operation @s ss_hudtmp /= @s ss_need
execute if score @s ss_ng matches 1.. if score @s ss_mneed matches 1.. run scoreboard players operation @s ss_hudtmp /= @s ss_mneed
execute if score @s ss_ng matches 0 if score @s ss_need matches 1.. run scoreboard players operation @s ss_hudfill = @s ss_hudtmp
execute if score @s ss_ng matches 1.. if score @s ss_mneed matches 1.. run scoreboard players operation @s ss_hudfill = @s ss_hudtmp
execute if score @s ss_hudfill matches 11.. run scoreboard players set @s ss_hudfill 10
execute if score @s ss_hudfill matches ..-1 run scoreboard players set @s ss_hudfill 0
execute if score @s ss_hudmode matches 2 run function shatteredseals:hud/show_detailed
execute unless score @s ss_hudmode matches 2 run function shatteredseals:hud/show_compact
