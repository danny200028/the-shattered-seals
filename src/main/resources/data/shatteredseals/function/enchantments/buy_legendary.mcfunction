# Server-authoritative level purchase. A failed purchase consumes nothing.
execute unless entity @s[level=30..] run return run tellraw @s {"text":"Need 30 vanilla XP levels for a legendary book.","color":"red"}
execute store result score @s ss_ebook_roll run random value 1..12
xp add @s -30 levels
execute if score @s ss_ebook_roll matches 1 run function shatteredseals:enchantments/give_soul_reaper
execute if score @s ss_ebook_roll matches 2 run function shatteredseals:enchantments/give_infernal_wrath
execute if score @s ss_ebook_roll matches 3 run function shatteredseals:enchantments/give_celestial_judgement
execute if score @s ss_ebook_roll matches 4 run function shatteredseals:enchantments/give_titans_resolve
execute if score @s ss_ebook_roll matches 5 run function shatteredseals:enchantments/give_immortal_spirit
execute if score @s ss_ebook_roll matches 6 run function shatteredseals:enchantments/give_voidwalker
execute if score @s ss_ebook_roll matches 7 run function shatteredseals:enchantments/give_worldbreaker
execute if score @s ss_ebook_roll matches 8 run function shatteredseals:enchantments/give_eternal_harvest
execute if score @s ss_ebook_roll matches 9 run function shatteredseals:enchantments/give_stormcaller
execute if score @s ss_ebook_roll matches 10 run function shatteredseals:enchantments/give_dragons_fury
execute if score @s ss_ebook_roll matches 11 run function shatteredseals:enchantments/give_guardian_angel
execute if score @s ss_ebook_roll matches 12 run function shatteredseals:enchantments/give_kings_execution
