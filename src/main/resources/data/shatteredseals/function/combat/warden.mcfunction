scoreboard players remove @s ss_guard 12
# Bastion Pulse: crowd control and defensive shield.
execute as @e[type=#shatteredseals:combat_targets,distance=..5] run effect give @s minecraft:slowness 4 2 true
effect give @s minecraft:resistance 6 1 true
effect give @s minecraft:absorption 8 0 true
execute if score @s ss_b21 matches 3.. run effect give @s minecraft:absorption 8 1 true
particle minecraft:enchant ~ ~1 ~ 2 1 2 0.2 60 force
scoreboard players set @s ss_burst_cd 240
title @s actionbar {"text":"BASTION PULSE  |  12s cooldown","color":"gold"}
