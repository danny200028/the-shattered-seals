# One-time migration for saves created before dungeon-only kill tracking.
# The legacy mob-kill statistic mixes animals, ordinary hostiles and dungeon mobs;
# their historical contributions cannot be separated precisely.
# Reset legacy kill-derived milestones and unspent RPG XP/NG+ XP for old saves.
# Retain character level, skills/points, unlocked masteries, boss victories, world and gear.
execute if score @s ss_kills matches 1.. run scoreboard players set @s ss_xp 0
execute if score @s ss_kills matches 1.. run scoreboard players set @s ss_mxp 0
scoreboard players set @s ss_dkills 0
scoreboard players set @s ss_kprev 0
scoreboard players set @s ss_rank 0
scoreboard players set @s ss_dkill_migrated 1
execute if score @s ss_kills matches 1.. run tellraw @s {"text":"Shattered Seals migration: old mixed mob-kill milestones and unspent RPG XP reset. Levels, skills, items, and story preserved. Only dungeon enemies now count.","color":"green"}
