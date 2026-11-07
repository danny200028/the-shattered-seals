scoreboard players add @s ss_dragon 1
execute if score @s ss_dragon matches 2.. run give @s minecraft:dragon_egg 1
execute if score @s ss_dragon matches 2.. run tellraw @s {"text":"Respawned dragon defeated: +1 Dragon Egg for a skill reset!","color":"light_purple"}
advancement revoke @s only shatteredseals:progression/dragon_slayer
