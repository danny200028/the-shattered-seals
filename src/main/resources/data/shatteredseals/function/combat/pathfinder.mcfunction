scoreboard players remove @s ss_focus 12
# Hunter's Mark: marks enemies near the ranger, grants speed and a brief damage buff.
execute as @e[type=#shatteredseals:combat_targets,distance=..12] run effect give @s minecraft:glowing 9 0 true
effect give @s minecraft:speed 7 1 true
effect give @s minecraft:strength 6 0 true
execute if score @s ss_b31 matches 3.. run effect give @s minecraft:jump_boost 6 1 true
particle minecraft:happy_villager ~ ~1 ~ 1 1 1 0.1 25 force
scoreboard players set @s ss_burst_cd 180
title @s actionbar {"text":"HUNTER'S MARK  |  9s cooldown","color":"green"}
