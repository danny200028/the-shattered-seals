execute unless score @s ss_points matches 1.. run tellraw @s {"text":"You need a skill point.","color":"red"}
execute if score @s ss_b32 matches 5.. run tellraw @s {"text":"Branch already rank 5.","color":"yellow"}
execute if score @s ss_points matches 1.. if score @s ss_b32 matches ..4 run function shatteredseals:talents/apply_8
