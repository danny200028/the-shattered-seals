execute at @s run effect give @e[type=!minecraft:player,type=!minecraft:item,distance=1..7] minecraft:glowing 15 0 true
effect give @s minecraft:fire_resistance 18 0 true
effect give @s minecraft:strength 12 1 true
tellraw @s {"text": "MASTERY: Elemental Dominion", "color": "light_purple"}
