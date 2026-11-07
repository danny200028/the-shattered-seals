effect give @s minecraft:fire_resistance 20 0 true
effect give @s minecraft:regeneration 5 1 true
particle minecraft:flame ~ ~1 ~ 1 1 1 0.12 45 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Ember Guard activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"ember_guard",ss_power:2}] run effect give @s minecraft:fire_resistance 40 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"ember_guard",ss_power:3}] run effect give @s minecraft:regeneration 10 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"ember_guard",ss_power:4}] run effect give @s minecraft:strength 12 1 true
