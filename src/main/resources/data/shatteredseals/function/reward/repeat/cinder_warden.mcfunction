loot give @s loot shatteredseals:repeat/cinder_warden
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.6 1.5
tellraw @s {"text": "Cinder Warden defeated again! A random Warden's Spoils item was awarded.", "color": "aqua"}
execute if predicate shatteredseals:trophy_chance run loot give @s loot shatteredseals:trophy/cinder_warden
