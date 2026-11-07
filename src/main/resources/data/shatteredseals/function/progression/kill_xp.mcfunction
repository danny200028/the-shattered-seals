scoreboard players operation @s ss_delta = @s ss_kills
scoreboard players operation @s ss_delta -= @s ss_kprev
execute if score @s ss_delta matches 1.. run function shatteredseals:progression/award_kills
scoreboard players operation @s ss_kprev = @s ss_kills
