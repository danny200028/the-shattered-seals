# Server-authoritative level purchase. A failed purchase consumes nothing.
execute unless entity @s[level=20..] run return run tellraw @s {"text":"Need 20 vanilla XP levels for a rare book.","color":"red"}
execute store result score @s ss_ebook_roll run random value 1..12
xp add @s -20 levels
execute if score @s ss_ebook_roll matches 1 run function shatteredseals:enchantments/give_vampiric_edge
execute if score @s ss_ebook_roll matches 2 run function shatteredseals:enchantments/give_frostbite
execute if score @s ss_ebook_roll matches 3 run function shatteredseals:enchantments/give_thunderstrike
execute if score @s ss_ebook_roll matches 4 run function shatteredseals:enchantments/give_guardian_aura
execute if score @s ss_ebook_roll matches 5 run function shatteredseals:enchantments/give_phoenix_skin
execute if score @s ss_ebook_roll matches 6 run function shatteredseals:enchantments/give_shadowstep
execute if score @s ss_ebook_roll matches 7 run function shatteredseals:enchantments/give_berserker
execute if score @s ss_ebook_roll matches 8 run function shatteredseals:enchantments/give_vein_seeker
execute if score @s ss_ebook_roll matches 9 run function shatteredseals:enchantments/give_soul_collector
execute if score @s ss_ebook_roll matches 10 run function shatteredseals:enchantments/give_hunters_precision
execute if score @s ss_ebook_roll matches 11 run function shatteredseals:enchantments/give_second_wind
execute if score @s ss_ebook_roll matches 12 run function shatteredseals:enchantments/give_magnetic_harvest
