scoreboard players set @s ss_upok 1
shatteredseals upgrade remember
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:bone 64
clear @s minecraft:nether_star 2
function shatteredseals:gear/gravekeeper/ascended
shatteredseals upgrade restore
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Gravekeeper upgraded to Ascended!","color":"green","italic":false}
