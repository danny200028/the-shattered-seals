scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:nether_star 0
execute store result score @s ss_metal run clear @s minecraft:netherite_ingot 0
execute if score @s ss_mat matches 1.. if score @s ss_metal matches 1.. run function shatteredseals:upgrade/kings_bane_to_awakened_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"King's Bane: Dormant to Awakened requires 1 nether star + 1 netherite ingot.","color":"yellow","italic":false}
