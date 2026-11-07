scoreboard players remove @s ss_focus 45
# Slot 4: Shadow Escape. Server-authorized, independent cooldown.
effect give @s minecraft:invisibility 8 0 true
effect give @s minecraft:speed 8 1 true
particle minecraft:smoke ~ ~1 ~ 1 1 1 0.1 40 force
scoreboard players set @s ss_burst_cd4 400
title @s actionbar {"text":"SHADOW ESCAPE | 20s cooldown","color":"aqua"}
