scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:bone 0
execute store result score @s ss_metal run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 64.. if score @s ss_metal matches 2.. run function shatteredseals:upgrade/gravekeeper_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Gravekeeper: Awakened to Ascended requires 64 bone + 2 nether star.","color":"yellow","italic":false}
