# Refresh rank-scaled NG+ effects once per second
execute if score @s ss_mp_guard matches 1 run effect give @s minecraft:resistance 2 0 true
execute if score @s ss_mp_guard matches 2 run effect give @s minecraft:resistance 2 1 true
execute if score @s ss_mp_guard matches 3 run effect give @s minecraft:resistance 2 2 true
execute if score @s ss_mp_guard matches 4 run effect give @s minecraft:resistance 2 3 true
execute if score @s ss_mp_guard matches 5 run effect give @s minecraft:resistance 2 4 true
execute if score @s ss_mp_haste matches 1 run effect give @s minecraft:haste 2 0 true
execute if score @s ss_mp_haste matches 2 run effect give @s minecraft:haste 2 1 true
execute if score @s ss_mp_haste matches 3 run effect give @s minecraft:haste 2 2 true
execute if score @s ss_mp_haste matches 4 run effect give @s minecraft:haste 2 3 true
execute if score @s ss_mp_haste matches 5 run effect give @s minecraft:haste 2 4 true
execute if score @s ss_mp_fury matches 1 run effect give @s minecraft:strength 2 0 true
execute if score @s ss_mp_fury matches 2 run effect give @s minecraft:strength 2 1 true
execute if score @s ss_mp_fury matches 3 run effect give @s minecraft:strength 2 2 true
execute if score @s ss_mp_fury matches 4 run effect give @s minecraft:strength 2 3 true
execute if score @s ss_mp_fury matches 5 run effect give @s minecraft:strength 2 4 true
