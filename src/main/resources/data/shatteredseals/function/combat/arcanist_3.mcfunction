scoreboard players remove @s ss_arcane 28
# Slot 3: Storm Spark. Server-authorized, independent cooldown.
execute as @e[type=#shatteredseals:combat_targets,distance=..7,limit=4,sort=nearest] run damage @s 4 minecraft:magic
particle minecraft:electric_spark ~ ~1 ~ 2 1 2 0.1 65 force
scoreboard players set @s ss_burst_cd3 260
title @s actionbar {"text":"STORM SPARK | 13s cooldown","color":"aqua"}
