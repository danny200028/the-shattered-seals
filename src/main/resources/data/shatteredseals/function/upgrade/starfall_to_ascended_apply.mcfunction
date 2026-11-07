scoreboard players set @s ss_upok 1
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:amethyst_shard 48
clear @s minecraft:nether_star 2
function shatteredseals:gear/starfall/ascended
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Starfall upgraded to Ascended!","color":"green","italic":false}
