scoreboard players remove @s ss_arcane 12
# Arcane Nova: magical burst around the caster, at the cost of a longer cooldown.
execute as @e[type=#shatteredseals:combat_targets,distance=..6] run damage @s 5 minecraft:magic
execute as @e[type=#shatteredseals:combat_targets,distance=..6] run effect give @s minecraft:weakness 5 0 true
particle minecraft:witch ~ ~1 ~ 2 1 2 0.3 90 force
execute if score @s ss_b41 matches 3.. run effect give @s minecraft:regeneration 4 0 true
scoreboard players set @s ss_burst_cd 220
title @s actionbar {"text":"ARCANE NOVA  |  11s cooldown","color":"light_purple"}
