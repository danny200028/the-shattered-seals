scoreboard players operation @s ss_mxp -= @s ss_mneed
scoreboard players add @s ss_master 1
scoreboard players add @s ss_mpoints 1
function shatteredseals:progression/recalculate
tellraw @s [{"text":"MASTERY RANK ","color":"light_purple"},{"score":{"name":"@s","objective":"ss_master"}},{"text":" | +1 mastery point","color":"gold"}]
