scoreboard players operation @s ss_xp -= @s ss_need
scoreboard players add @s ss_level 1
scoreboard players add @s ss_points 3
function shatteredseals:progression/recalculate
tellraw @s [{"text":"LEVEL UP! Character level ","color":"gold"},{"score":{"name":"@s","objective":"ss_level"}},{"text":" | +3 skill points","color":"green"}]
