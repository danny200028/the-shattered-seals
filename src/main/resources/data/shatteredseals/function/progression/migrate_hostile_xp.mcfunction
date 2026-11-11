# Existing world: preserve earned XP, levels, skills and saved milestone totals.
# Seed the new hostile-XP counter from prior kills; treat those as already paid.
# Past ss_dkills may include non-dungeon kills from an earlier release;
# precise historical locations were not recorded and cannot be separated.
scoreboard players add @s ss_dkills 0
scoreboard players operation @s ss_hkills = @s ss_dkills
scoreboard players operation @s ss_hprev = @s ss_hkills
scoreboard players set @s ss_hkill_migrated 1
tellraw @s {"text":"Shattered Seals: hostile kills now grant skill XP anywhere; only dungeon enemies advance milestones. Previous levels, XP and saved kills preserved.","color":"green"}
