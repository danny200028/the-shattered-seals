scoreboard players operation @s ss_pending = @s ss_switch
execute if score @s ss_pending = @s ss_path run tellraw @s {"text":"That class is already active.","color":"yellow"}
execute unless score @s ss_pending = @s ss_path run function shatteredseals:classes/apply
