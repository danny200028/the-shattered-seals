# Clamp normal progression; preserve earned skill points and mastery rank.
execute if score @s ss_level matches 61.. run scoreboard players set @s ss_level 60
scoreboard players set @s ss_xp 0
scoreboard players set @s ss_need 2147483647
