scoreboard players remove @s ss_focus 28
# Slot 3: Predator Focus. Server-authorized, independent cooldown.
effect give @s minecraft:strength 8 0 true
effect give @s minecraft:night_vision 10 0 true
particle minecraft:happy_villager ~ ~1 ~ 0.7 0.6 0.7 0.1 20 force
scoreboard players set @s ss_burst_cd3 220
title @s actionbar {"text":"PREDATOR FOCUS | 11s cooldown","color":"aqua"}
