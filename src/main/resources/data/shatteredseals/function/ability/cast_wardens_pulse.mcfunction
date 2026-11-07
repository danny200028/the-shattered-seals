effect give @s minecraft:resistance 10 1 true
effect give @e[type=#shatteredseals:spell_targets,distance=1..6] minecraft:levitation 2 1 true
particle minecraft:totem_of_undying ~ ~1 ~ 1 0.5 1 0.15 35 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Wardens Pulse activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"wardens_pulse",ss_power:2}] run effect give @s minecraft:absorption 15 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"wardens_pulse",ss_power:3}] run effect give @s minecraft:regeneration 8 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"wardens_pulse",ss_power:4}] run effect give @s minecraft:strength 14 1 true
