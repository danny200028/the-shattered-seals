scoreboard players remove @s ss_fury 45
# Slot 4: Last Stand. Server-authorized, independent cooldown.
effect give @s minecraft:absorption 12 2 true
effect give @s minecraft:regeneration 6 0 true
particle minecraft:heart ~ ~1 ~ 1 1 1 0.1 25 force
scoreboard players set @s ss_burst_cd4 500
title @s actionbar {"text":"LAST STAND | 25s cooldown","color":"aqua"}
