scoreboard players remove @s ss_fury 28
# Slot 3: Earthbreaker. Server-authorized, independent cooldown.
execute as @e[type=#shatteredseals:combat_targets,distance=..5] run damage @s 4 minecraft:player_attack
execute as @e[type=#shatteredseals:combat_targets,distance=..5] run effect give @s minecraft:slowness 3 1 true
particle minecraft:crit ~ ~0.5 ~ 2 0.4 2 0.2 45 force
scoreboard players set @s ss_burst_cd3 260
title @s actionbar {"text":"EARTHBREAKER | 13s cooldown","color":"aqua"}
