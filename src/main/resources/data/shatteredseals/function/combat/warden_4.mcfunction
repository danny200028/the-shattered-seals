scoreboard players remove @s ss_guard 45
# Slot 4: Undying Oath. Server-authorized, independent cooldown.
effect give @s minecraft:resistance 10 2 true
effect give @s minecraft:regeneration 10 1 true
particle minecraft:totem_of_undying ~ ~1 ~ 1 1 1 0.15 45 force
scoreboard players set @s ss_burst_cd4 600
title @s actionbar {"text":"UNDYING OATH | 30s cooldown","color":"aqua"}
