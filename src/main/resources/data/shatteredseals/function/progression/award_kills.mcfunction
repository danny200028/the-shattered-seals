scoreboard players set #five ss_delta 5
scoreboard players operation @s ss_delta *= #five ss_delta
execute if score @s ss_level matches ..59 run scoreboard players operation @s ss_xp += @s ss_delta
execute if score @s ss_level matches 60.. if score @s ss_ng matches 1.. run scoreboard players operation @s ss_mxp += @s ss_delta
