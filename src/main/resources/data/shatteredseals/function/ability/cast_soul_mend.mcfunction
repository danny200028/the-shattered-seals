effect give @s minecraft:regeneration 10 1 true
effect give @s minecraft:absorption 25 1 true
effect give @s minecraft:hunger 15 0 true
particle minecraft:soul ~ ~1 ~ 0.5 0.8 0.5 0.05 55 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Soul Mend activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"soul_mend",ss_power:2}] run effect give @s minecraft:regeneration 15 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"soul_mend",ss_power:3}] run effect give @s minecraft:absorption 35 2 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"soul_mend",ss_power:4}] run effect give @s minecraft:resistance 15 1 true
