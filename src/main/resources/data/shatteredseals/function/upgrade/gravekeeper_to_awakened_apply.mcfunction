scoreboard players set @s ss_upok 1
item replace entity @s weapon.mainhand with minecraft:air
clear @s minecraft:bone 32
clear @s minecraft:netherite_ingot 1
function shatteredseals:gear/gravekeeper/awakened
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.9 1
tellraw @s {"text":"Gravekeeper upgraded to Awakened!","color":"green","italic":false}
