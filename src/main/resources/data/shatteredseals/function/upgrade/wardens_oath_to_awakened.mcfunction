scoreboard players set @s ss_upok 2
execute store result score @s ss_mat run clear @s minecraft:echo_shard 0
execute store result score @s ss_metal run clear @s minecraft:netherite_ingot 0
execute if score @s ss_mat matches 4.. if score @s ss_metal matches 1.. run function shatteredseals:upgrade/wardens_oath_to_awakened_apply
execute if score @s ss_upok matches 2 run tellraw @s {"text":"Warden's Oath: Dormant to Awakened requires 4 echo shard + 1 netherite ingot.","color":"yellow","italic":false}
