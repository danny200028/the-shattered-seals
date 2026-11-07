scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 4.. run function shatteredseals:upgrade/kings_bane_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"King's Bane: Awakened to Ascended requires 2 nether star + 2 nether star.","color":"yellow","italic":false}
