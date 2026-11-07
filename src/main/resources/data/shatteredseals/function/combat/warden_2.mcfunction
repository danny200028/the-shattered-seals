scoreboard players remove @s ss_guard 20
# Slot 2: Guardian Aura. Server-authorized, independent cooldown.
effect give @s minecraft:absorption 12 1 true
effect give @s minecraft:resistance 8 0 true
particle minecraft:enchant ~ ~1 ~ 1.2 1 1.2 0.15 40 force
scoreboard players set @s ss_burst_cd2 260
title @s actionbar {"text":"GUARDIAN AURA | 13s cooldown","color":"aqua"}
