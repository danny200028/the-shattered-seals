effect give @s minecraft:haste 45 1 true
effect give @s minecraft:night_vision 45 0 true
effect give @s minecraft:water_breathing 25 0 true
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Delvers Draft activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"delvers_draft",ss_power:2}] run effect give @s minecraft:haste 45 2 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"delvers_draft",ss_power:3}] run effect give @s minecraft:water_breathing 50 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"delvers_draft",ss_power:4}] run effect give @s minecraft:resistance 25 1 true
