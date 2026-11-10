# Server-authoritative fixed XP-point purchase. A failed purchase consumes nothing; Creative is free.
execute unless entity @s[gamemode=creative] unless entity @s[level=10..] run return run tellraw @s {"text":"Need 160 XP points (equivalent to level 10) for a common book.","color":"red"}
execute store result score @s ss_ebook_roll run random value 1..12
execute unless entity @s[gamemode=creative] run xp add @s -160 points
execute if score @s ss_ebook_roll matches 1 run function shatteredseals:enchantments/give_keen_edge
execute if score @s ss_ebook_roll matches 2 run function shatteredseals:enchantments/give_stagger
execute if score @s ss_ebook_roll matches 3 run function shatteredseals:enchantments/give_lifebloom
execute if score @s ss_ebook_roll matches 4 run function shatteredseals:enchantments/give_stoneguard
execute if score @s ss_ebook_roll matches 5 run function shatteredseals:enchantments/give_fleetfoot
execute if score @s ss_ebook_roll matches 6 run function shatteredseals:enchantments/give_deep_miner
execute if score @s ss_ebook_roll matches 7 run function shatteredseals:enchantments/give_lumberjack
execute if score @s ss_ebook_roll matches 8 run function shatteredseals:enchantments/give_harvest_blessing
execute if score @s ss_ebook_roll matches 9 run function shatteredseals:enchantments/give_durable
execute if score @s ss_ebook_roll matches 10 run function shatteredseals:enchantments/give_archers_focus
execute if score @s ss_ebook_roll matches 11 run function shatteredseals:enchantments/give_aqua_step
execute if score @s ss_ebook_roll matches 12 run function shatteredseals:enchantments/give_treasure_sense
