scoreboard players set @s ss_fury 100
scoreboard players set @s ss_guard 100
scoreboard players set @s ss_focus 100
scoreboard players set @s ss_arcane 100
scoreboard players operation @s ss_damage_prev = @s ss_damage_now
scoreboard players operation @s ss_taken_prev = @s ss_taken_now
scoreboard players set @s ss_resinit 1
scoreboard players set @s ss_resource_clock 0
scoreboard players operation @s ss_sprint_prev = @s ss_sprint_now
scoreboard players set @s ss_sprint_delta 0
