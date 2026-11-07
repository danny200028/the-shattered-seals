scoreboard players remove @s ss_fury 20
# Slot 2: Rallying Roar. Server-authorized, independent cooldown.
effect give @s minecraft:strength 8 0 true
effect give @s minecraft:resistance 8 0 true
particle minecraft:flame ~ ~1 ~ 1 1 1 0.1 35 force
scoreboard players set @s ss_burst_cd2 220
title @s actionbar {"text":"RALLYING ROAR | 11s cooldown","color":"aqua"}
