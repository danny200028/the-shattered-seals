loot give @s loot shatteredseals:repeat/smuggler_king
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.6 1.5
tellraw @s {"text": "Smuggler King defeated again! A random Warden's Spoils item was awarded.", "color": "aqua"}
execute if predicate shatteredseals:trophy_chance run loot give @s loot shatteredseals:trophy/smuggler_king
execute if score @s ss_ng matches 1.. run function shatteredseals:progression/boss_xp
