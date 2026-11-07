scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:amethyst_shard 0
execute store result score @s ss_metal run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 48.. if score @s ss_metal matches 2.. run function shatteredseals:upgrade/starfall_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Starfall: Awakened to Ascended requires 48 amethyst shard + 2 nether star.","color":"yellow","italic":false}
