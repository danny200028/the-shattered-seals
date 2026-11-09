scoreboard players set @s ss_upok 1
shatteredseals upgrade remember
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:echo_shard 4
clear @s minecraft:netherite_ingot 1
function shatteredseals:gear/wardens_oath/awakened
shatteredseals upgrade restore
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Warden's Oath upgraded to Awakened!","color":"green","italic":false}
