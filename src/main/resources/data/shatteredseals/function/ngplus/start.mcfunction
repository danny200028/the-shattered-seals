execute unless score @s ss_level matches 60.. run return run tellraw @s {"text":"New Game+ requires character level 60.","color":"red"}
execute unless score @s ss_vk_king matches 1.. run return run tellraw @s {"text":"Defeat the Hollow King to unlock New Game+.","color":"red"}
execute if score @s ss_ngchapter matches 1..6 run return run tellraw @s {"text":"Complete your current New Game+ circuit first.","color":"yellow"}
scoreboard players add @s ss_ng 1
scoreboard players set @s ss_ngchapter 1
# A new circuit requires the rune puzzle and keeper challenge in every chapter again.
scoreboard players set @s ss_rune1 0
scoreboard players set @s ss_side1 0
scoreboard players set @s ss_rune2 0
scoreboard players set @s ss_side2 0
scoreboard players set @s ss_rune3 0
scoreboard players set @s ss_side3 0
scoreboard players set @s ss_rune4 0
scoreboard players set @s ss_side4 0
scoreboard players set @s ss_rune5 0
scoreboard players set @s ss_side5 0
scoreboard players set @s ss_rune6 0
scoreboard players set @s ss_side6 0
tellraw @s {"text":"New Game+ begins at the Smugglers' Den. Replay all six dungeons for mastery XP.","color":"light_purple"}
