scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:blaze_rod 0
execute store result score @s ss_metal run clear @s minecraft:netherite_ingot 0
execute if score @s ss_mat matches 16.. if score @s ss_metal matches 1.. run function shatteredseals:upgrade/emberbound_to_awakened_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Emberbound Blade: Dormant to Awakened requires 16 blaze rod + 1 netherite ingot.","color":"yellow","italic":false}
