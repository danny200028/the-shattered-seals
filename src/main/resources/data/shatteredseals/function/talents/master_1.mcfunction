execute if score @s ss_a11 matches 1.. run tellraw @s {"text":"Mastery already unlocked.","color":"yellow"}
execute if score @s ss_a11 matches 0 unless score @s ss_level matches 50.. run tellraw @s {"text":"Mastery requires level 50.","color":"red"}
execute if score @s ss_a11 matches 0 unless score @s ss_kills matches 100.. run tellraw @s {"text":"Mastery requires 100 mob kills.","color":"red"}
execute if score @s ss_a11 matches 0 unless score @s ss_b11 matches 5.. run tellraw @s {"text":"Mastery requires rank 5 in this branch.","color":"red"}
execute if score @s ss_a11 matches 0 unless score @s ss_points matches 8.. run tellraw @s {"text":"Mastery costs 8 unspent skill points.","color":"red"}
execute if score @s ss_a11 matches 0 if score @s ss_level matches 50.. if score @s ss_kills matches 100.. if score @s ss_b11 matches 5.. if score @s ss_points matches 8.. run function shatteredseals:talents/master_apply_1
