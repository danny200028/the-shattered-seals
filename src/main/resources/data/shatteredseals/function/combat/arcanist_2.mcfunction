scoreboard players remove @s ss_arcane 20
# Slot 2: Frost Ring. Server-authorized, independent cooldown.
execute as @e[type=#shatteredseals:combat_targets,distance=..5] run effect give @s minecraft:slowness 6 2 true
particle minecraft:snowflake ~ ~1 ~ 2 0.5 2 0.1 50 force
scoreboard players set @s ss_burst_cd2 200
title @s actionbar {"text":"FROST RING | 10s cooldown","color":"aqua"}
