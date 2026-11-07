execute if score @s ss_ability_cd matches 1.. run tellraw @s {"text":"Relic still recharging!","color":"red"}
execute if score @s ss_ability_cd matches 0 run function shatteredseals:ability/cast_echo_ping
