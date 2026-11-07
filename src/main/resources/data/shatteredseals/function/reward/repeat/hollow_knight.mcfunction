loot give @s loot shatteredseals:repeat/hollow_knight
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.6 1.5
tellraw @s {"text": "Hollow Knight defeated again! A random Warden's Spoils item was awarded.", "color": "aqua"}
execute if predicate shatteredseals:trophy_chance run loot give @s loot shatteredseals:trophy/hollow_knight
