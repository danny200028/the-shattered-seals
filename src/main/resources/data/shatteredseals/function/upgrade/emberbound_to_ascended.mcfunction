scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:blaze_rod 0
execute store result score @s ss_metal run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 32.. if score @s ss_metal matches 2.. run function shatteredseals:upgrade/emberbound_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Emberbound Blade: Awakened to Ascended requires 32 blaze rod + 2 nether star.","color":"yellow","italic":false}
