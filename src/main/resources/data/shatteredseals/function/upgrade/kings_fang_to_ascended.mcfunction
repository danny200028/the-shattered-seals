scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:emerald 0
execute store result score @s ss_metal run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 48.. if score @s ss_metal matches 2.. run function shatteredseals:upgrade/kings_fang_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"King's Fang: Awakened to Ascended requires 48 emerald + 2 nether star.","color":"yellow","italic":false}
