scoreboard players remove @s ss_guard 28
# Slot 3: Shield Rebuke. Server-authorized, independent cooldown.
execute as @e[type=#shatteredseals:combat_targets,distance=..4] run damage @s 3 minecraft:magic
execute as @e[type=#shatteredseals:combat_targets,distance=..4] run effect give @s minecraft:weakness 6 1 true
particle minecraft:crit ~ ~1 ~ 1.4 0.6 1.4 0.1 35 force
scoreboard players set @s ss_burst_cd3 220
title @s actionbar {"text":"SHIELD REBUKE | 11s cooldown","color":"aqua"}
