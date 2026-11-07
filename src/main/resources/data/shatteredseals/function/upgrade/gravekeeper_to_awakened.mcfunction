scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:bone 0
execute store result score @s ss_metal run clear @s minecraft:netherite_ingot 0
execute if score @s ss_mat matches 32.. if score @s ss_metal matches 1.. run function shatteredseals:upgrade/gravekeeper_to_awakened_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Gravekeeper: Dormant to Awakened requires 32 bone + 1 netherite ingot.","color":"yellow","italic":false}
