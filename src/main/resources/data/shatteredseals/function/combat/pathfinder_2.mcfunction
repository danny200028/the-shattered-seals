scoreboard players remove @s ss_focus 20
# Slot 2: Windstep. Server-authorized, independent cooldown.
effect give @s minecraft:speed 8 2 true
effect give @s minecraft:jump_boost 8 1 true
particle minecraft:cloud ~ ~0.5 ~ 1 0.4 1 0.05 25 force
scoreboard players set @s ss_burst_cd2 160
title @s actionbar {"text":"WINDSTEP | 8s cooldown","color":"aqua"}
