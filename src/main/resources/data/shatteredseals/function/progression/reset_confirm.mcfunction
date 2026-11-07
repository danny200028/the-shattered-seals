scoreboard players set @s ss_egg 0
execute store success score @s ss_egg run clear @s minecraft:dragon_egg 1
execute if score @s ss_egg matches 1 run function shatteredseals:progression/reset_apply
execute unless score @s ss_egg matches 1 run tellraw @s {"text":"Skill reset failed: one Dragon Egg required.","color":"red"}
