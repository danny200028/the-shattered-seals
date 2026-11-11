scoreboard players set @s ss_level 1
scoreboard players set @s ss_xp 0
scoreboard players set @s ss_points 0
scoreboard players set @s ss_spent 0
scoreboard players set @s ss_master 0
scoreboard players set @s ss_mxp 0
scoreboard players set @s ss_mpoints 0
scoreboard players set @s ss_t_van 0
scoreboard players set @s ss_t_ward 0
scoreboard players set @s ss_t_path 0
scoreboard players set @s ss_t_arc 0
# Fresh characters track only hostile kills, anywhere, with the existing save-compatible score.
scoreboard players set @s ss_dkills 0
scoreboard players set @s ss_kprev 0
scoreboard players set @s ss_dkill_migrated 1
function shatteredseals:progression/recalculate
scoreboard players set @s ss_b11 0
scoreboard players set @s ss_a11 0
scoreboard players set @s ss_b12 0
scoreboard players set @s ss_a12 0
scoreboard players set @s ss_b13 0
scoreboard players set @s ss_a13 0
scoreboard players set @s ss_b21 0
scoreboard players set @s ss_a21 0
scoreboard players set @s ss_b22 0
scoreboard players set @s ss_a22 0
scoreboard players set @s ss_b23 0
scoreboard players set @s ss_a23 0
scoreboard players set @s ss_b31 0
scoreboard players set @s ss_a31 0
scoreboard players set @s ss_b32 0
scoreboard players set @s ss_a32 0
scoreboard players set @s ss_b33 0
scoreboard players set @s ss_a33 0
scoreboard players set @s ss_b41 0
scoreboard players set @s ss_a41 0
scoreboard players set @s ss_b42 0
scoreboard players set @s ss_a42 0
scoreboard players set @s ss_b43 0
scoreboard players set @s ss_a43 0

# Explicit zero starting ranks for New Game+ perk purchases and refunds
scoreboard players set @s ss_mp_guard 0
scoreboard players set @s ss_mp_haste 0
scoreboard players set @s ss_mp_fury 0
scoreboard players set @s ss_reset_confirm 0
scoreboard players set @s ss_reset_timer 0
