scoreboard players set @s ss_upok 0
# Match only a specific relic and stage in the PLAYER MAIN HAND.
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"emberbound",ss_tier:"dormant"}] run function shatteredseals:upgrade/emberbound_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"emberbound",ss_tier:"awakened"}] run function shatteredseals:upgrade/emberbound_to_ascended
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"kings_fang",ss_tier:"dormant"}] run function shatteredseals:upgrade/kings_fang_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"kings_fang",ss_tier:"awakened"}] run function shatteredseals:upgrade/kings_fang_to_ascended
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"gravekeeper",ss_tier:"dormant"}] run function shatteredseals:upgrade/gravekeeper_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"gravekeeper",ss_tier:"awakened"}] run function shatteredseals:upgrade/gravekeeper_to_ascended
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"starfall",ss_tier:"dormant"}] run function shatteredseals:upgrade/starfall_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"starfall",ss_tier:"awakened"}] run function shatteredseals:upgrade/starfall_to_ascended
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"wardens_oath",ss_tier:"dormant"}] run function shatteredseals:upgrade/wardens_oath_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"wardens_oath",ss_tier:"awakened"}] run function shatteredseals:upgrade/wardens_oath_to_ascended
execute if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"kings_bane",ss_tier:"dormant"}] run function shatteredseals:upgrade/kings_bane_to_awakened
execute if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"kings_bane",ss_tier:"awakened"}] run function shatteredseals:upgrade/kings_bane_to_ascended
execute if score @s ss_upok matches 0 run tellraw @s {"text":"Hold a Dormant or Awakened Warden Relic in your main hand, then use /trigger ss_upgrade set 1.","color":"red","italic":false}
