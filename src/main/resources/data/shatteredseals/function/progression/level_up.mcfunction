# v1.0.37: level 60 is the maximum normal character level.
execute if score @s ss_level matches ..59 run function shatteredseals:progression/level_up_apply
execute if score @s ss_level matches 60.. run function shatteredseals:progression/level_cap
