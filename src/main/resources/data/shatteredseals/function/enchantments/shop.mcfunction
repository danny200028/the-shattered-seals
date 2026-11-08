execute if score @s ss_ebook matches 1 run function shatteredseals:enchantments/buy_common
execute if score @s ss_ebook matches 2 run function shatteredseals:enchantments/buy_rare
execute if score @s ss_ebook matches 3 run function shatteredseals:enchantments/buy_legendary
scoreboard players set @s ss_ebook 0
