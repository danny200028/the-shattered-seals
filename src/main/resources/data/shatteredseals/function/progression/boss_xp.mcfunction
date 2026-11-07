execute if score @s ss_level matches ..59 run scoreboard players add @s ss_xp 150
execute if score @s ss_level matches 60.. if score @s ss_ng matches 1.. run scoreboard players add @s ss_mxp 150
# Match the added challenge across later circuits, but cap progression rewards.
execute if score @s ss_level matches 60.. if score @s ss_ng matches 2.. run scoreboard players add @s ss_mxp 50
execute if score @s ss_level matches 60.. if score @s ss_ng matches 3.. run scoreboard players add @s ss_mxp 50
execute if score @s ss_level matches 60.. if score @s ss_ng matches 4.. run scoreboard players add @s ss_mxp 50
