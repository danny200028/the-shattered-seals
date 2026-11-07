execute positioned ^ ^ ^5 if block ~ ~ ~ minecraft:air if block ~ ~1 ~ minecraft:air run tp @s ~ ~ ~
effect give @s minecraft:slow_falling 12 0 true
particle minecraft:reverse_portal ~ ~1 ~ 0.5 0.5 0.5 0.2 70 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Rift Step activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"rift_step",ss_power:2}] run effect give @s minecraft:speed 12 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"rift_step",ss_power:3}] run effect give @s minecraft:invisibility 8 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"rift_step",ss_power:4}] run effect give @s minecraft:absorption 20 1 true
