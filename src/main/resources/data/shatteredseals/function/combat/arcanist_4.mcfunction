scoreboard players remove @s ss_arcane 45
# Slot 4: Arcane Overdrive. Server-authorized, independent cooldown.
effect give @s minecraft:strength 10 1 true
effect give @s minecraft:resistance 8 0 true
particle minecraft:witch ~ ~1 ~ 1.5 1 1.5 0.2 80 force
scoreboard players set @s ss_burst_cd4 500
title @s actionbar {"text":"ARCANE OVERDRIVE | 25s cooldown","color":"aqua"}
