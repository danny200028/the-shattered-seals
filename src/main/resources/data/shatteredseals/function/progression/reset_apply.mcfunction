# Refund all invested NG+ mastery points without changing mastery rank.
scoreboard players operation @s ss_mpoints += @s ss_mp_guard
scoreboard players set @s ss_mp_guard 0
scoreboard players operation @s ss_mpoints += @s ss_mp_haste
scoreboard players set @s ss_mp_haste 0
scoreboard players operation @s ss_mpoints += @s ss_mp_fury
scoreboard players set @s ss_mp_fury 0
scoreboard players operation @s ss_points += @s ss_spent
scoreboard players set @s ss_spent 0
scoreboard players set @s ss_t_van 0
scoreboard players set @s ss_t_ward 0
scoreboard players set @s ss_t_path 0
scoreboard players set @s ss_t_arc 0
tellraw @s {"text":"Skill points, including all purchased mastery unlocks, refunded. One Dragon Egg consumed. Character level and class retained.","color":"green"}
scoreboard players set @s ss_b11 0
scoreboard players set @s ss_a11 0
scoreboard players set @s ss_b12 0
scoreboard players set @s ss_a12 0
scoreboard players set @s ss_b13 0
scoreboard players set @s ss_a13 0
scoreboard players set @s ss_b21 0
scoreboard players set @s ss_a21 0
scoreboard players set @s ss_b22 0
scoreboard players set @s ss_a22 0
scoreboard players set @s ss_b23 0
scoreboard players set @s ss_a23 0
scoreboard players set @s ss_b31 0
scoreboard players set @s ss_a31 0
scoreboard players set @s ss_b32 0
scoreboard players set @s ss_a32 0
scoreboard players set @s ss_b33 0
scoreboard players set @s ss_a33 0
scoreboard players set @s ss_b41 0
scoreboard players set @s ss_a41 0
scoreboard players set @s ss_b42 0
scoreboard players set @s ss_a42 0
scoreboard players set @s ss_b43 0
scoreboard players set @s ss_a43 0
