scoreboard players set @s ss_upok 1
shatteredseals upgrade remember
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:blaze_rod 32
clear @s minecraft:nether_star 2
function shatteredseals:gear/emberbound/ascended
shatteredseals upgrade restore
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Emberbound Blade upgraded to Ascended!","color":"green","italic":false}
