effect give @e[type=#shatteredseals:spell_targets,distance=1..12] minecraft:slowness 12 3 true
effect give @s minecraft:speed 8 0 true
particle minecraft:snowflake ~ ~1 ~ 1 1 1 0.12 60 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Frost Snare activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"frost_snare",ss_power:2}] run effect give @s minecraft:resistance 10 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"frost_snare",ss_power:3}] run effect give @s minecraft:strength 10 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"frost_snare",ss_power:4}] run effect give @s minecraft:absorption 18 1 true
