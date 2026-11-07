effect give @s minecraft:invisibility 15 0 true
effect give @s minecraft:night_vision 20 0 true
effect give @s minecraft:weakness 15 0 true
particle minecraft:large_smoke ~ ~1 ~ 0.5 0.5 0.5 0.08 50 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Shadow Cloak activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"shadow_cloak",ss_power:2}] run effect give @s minecraft:speed 12 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"shadow_cloak",ss_power:3}] run effect give @s minecraft:jump_boost 12 1 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"shadow_cloak",ss_power:4}] run effect give @s minecraft:absorption 12 2 true
