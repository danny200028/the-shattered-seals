scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:echo_shard 0
execute store result score @s ss_metal run clear @s minecraft:nether_star 0
execute if score @s ss_mat matches 8.. if score @s ss_metal matches 2.. run function shatteredseals:upgrade/wardens_oath_to_ascended_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Warden's Oath: Awakened to Ascended requires 8 echo shard + 2 nether star.","color":"yellow","italic":false}
