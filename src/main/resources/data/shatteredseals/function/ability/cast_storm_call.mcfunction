execute at @s as @e[type=#shatteredseals:spell_targets,distance=2..15,sort=nearest,limit=1] at @s run summon minecraft:lightning_bolt ~ ~ ~
effect give @s minecraft:resistance 5 0 true
particle minecraft:electric_spark ~ ~1 ~ 0.5 0.8 0.5 0.1 55 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Storm Call activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"storm_call",ss_power:2}] run effect give @s minecraft:speed 12 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"storm_call",ss_power:3}] run effect give @s minecraft:resistance 15 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"storm_call",ss_power:4}] run effect give @s minecraft:absorption 20 1 true
