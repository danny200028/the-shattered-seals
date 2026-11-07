scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:amethyst_shard 0
execute store result score @s ss_metal run clear @s minecraft:netherite_ingot 0
execute if score @s ss_mat matches 24.. if score @s ss_metal matches 1.. run function shatteredseals:upgrade/starfall_to_awakened_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Starfall: Dormant to Awakened requires 24 amethyst shard + 1 netherite ingot.","color":"yellow","italic":false}
