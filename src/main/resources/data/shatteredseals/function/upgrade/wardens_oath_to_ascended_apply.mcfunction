scoreboard players set @s ss_upok 1
shatteredseals upgrade remember
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:echo_shard 8
clear @s minecraft:nether_star 2
function shatteredseals:gear/wardens_oath/ascended
shatteredseals upgrade restore
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Warden's Oath upgraded to Ascended!","color":"green","italic":false}
