execute unless score @s ss_points matches 1.. run tellraw @s {"text":"Not enough skill points.","color":"red"}
execute if score @s ss_t_van matches 5.. run tellraw @s {"text":"Talent already maxed (5/5).","color":"yellow"}
execute if score @s ss_points matches 1.. if score @s ss_t_van matches ..4 run function shatteredseals:progression/apply_1
