# Server-side class resources, persistent across class switching.
execute unless score @s ss_resinit matches 1 run function shatteredseals:combat/resource_init
scoreboard players operation @s ss_damage_delta = @s ss_damage_now
scoreboard players operation @s ss_damage_delta -= @s ss_damage_prev
scoreboard players operation @s ss_damage_prev = @s ss_damage_now
scoreboard players operation @s ss_taken_delta = @s ss_taken_now
scoreboard players operation @s ss_taken_delta -= @s ss_taken_prev
scoreboard players operation @s ss_taken_prev = @s ss_taken_now
# Damage statistics track cumulative amounts. Only positive deltas grant resource.
# A single damage event within a tick generates one burst, preventing large hits from overflowing the resource meter.
execute if score @s ss_damage_delta matches ..-1 run scoreboard players set @s ss_damage_delta 0
execute if score @s ss_taken_delta matches ..-1 run scoreboard players set @s ss_taken_delta 0
execute if score @s ss_path matches 1 if score @s ss_damage_delta matches 1.. run scoreboard players add @s ss_fury 8
execute if score @s ss_path matches 2 if score @s ss_taken_delta matches 1.. run scoreboard players add @s ss_guard 10
# Pathfinder Focus is earned from actual sprint distance, not player NBT.
scoreboard players operation @s ss_sprint_delta = @s ss_sprint_now
scoreboard players operation @s ss_sprint_delta -= @s ss_sprint_prev
scoreboard players operation @s ss_sprint_prev = @s ss_sprint_now
execute if score @s ss_sprint_delta matches ..-1 run scoreboard players set @s ss_sprint_delta 0
scoreboard players add @s ss_resource_clock 1
execute if score @s ss_path matches 3 if score @s ss_sprint_delta matches 1.. if score @s ss_resource_clock matches 20.. run scoreboard players add @s ss_focus 3
execute if score @s ss_resource_clock matches 20.. run function shatteredseals:combat/resource_regen
execute if score @s ss_resource_clock matches 20.. run scoreboard players set @s ss_resource_clock 0
execute if score @s ss_fury matches 101.. run scoreboard players set @s ss_fury 100
execute if score @s ss_guard matches 101.. run scoreboard players set @s ss_guard 100
execute if score @s ss_focus matches 101.. run scoreboard players set @s ss_focus 100
execute if score @s ss_arcane matches 101.. run scoreboard players set @s ss_arcane 100
