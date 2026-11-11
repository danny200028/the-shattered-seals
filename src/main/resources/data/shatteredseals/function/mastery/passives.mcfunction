# Runs only after progression/passives resets all effect contribution scores.
# New Game+ perk rank contributes its full number of levels (1 through 5).
execute if score @s ss_mp_guard matches 1.. run scoreboard players operation @s ss_fx_res += @s ss_mp_guard
execute if score @s ss_mp_haste matches 1.. run scoreboard players operation @s ss_fx_haste += @s ss_mp_haste
execute if score @s ss_mp_fury matches 1.. run scoreboard players operation @s ss_fx_str += @s ss_mp_fury
