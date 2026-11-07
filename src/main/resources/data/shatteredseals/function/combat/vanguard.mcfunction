scoreboard players remove @s ss_fury 12
# Blood Cleave: short-range AOE and brief sustain. Damage is applied only to nearby non-player living mobs.
execute as @e[type=#shatteredseals:combat_targets,distance=..4] run damage @s 6 minecraft:player_attack
particle minecraft:sweep_attack ~ ~1 ~ 1.8 0.5 1.8 0.2 18 force
effect give @s minecraft:regeneration 3 0 true
execute if score @s ss_b11 matches 3.. run effect give @s minecraft:strength 4 0 true
scoreboard players set @s ss_burst_cd 160
title @s actionbar {"text":"BLOOD CLEAVE  |  8s cooldown","color":"red"}
