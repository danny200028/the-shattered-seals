effect give @e[type=#shatteredseals:spell_targets,distance=1..20] minecraft:glowing 12 0 true
effect give @s minecraft:night_vision 18 0 true
particle minecraft:sonic_boom ~ ~1 ~ 0 0 0 0 1 force
scoreboard players set @s ss_ability_cd 400
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 0.8
tellraw @s {"text": "Echo Ping activated! Cooldown: 20 seconds.", "color": "light_purple"}
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"echo_ping",ss_power:2}] run effect give @s minecraft:speed 14 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"echo_ping",ss_power:3}] run effect give @s minecraft:resistance 12 0 true
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"echo_ping",ss_power:4}] run effect give @s minecraft:absorption 18 1 true
