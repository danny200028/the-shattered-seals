# Passive skill buffs are aggregated first, then applied once per effect.
# Multiple separately-earned effects stack by level: e.g. Haste I + Haste I = Haste II.
# Buffs cap at level V. Only the selected class contributes class skills; NG+ perks remain universal.
# Refreshed every 80 ticks (4s) by progression/tick. 16s ordinary effects, 30s Night Vision
# keep a wide safety margin, including Minecraft's last-10-seconds Night Vision flicker.
# Temporary active combat abilities remain on their own cooldowns and are not auto-refreshed.
scoreboard players set @s ss_fx_str 0
scoreboard players set @s ss_fx_res 0
scoreboard players set @s ss_fx_spd 0
scoreboard players set @s ss_fx_haste 0
scoreboard players set @s ss_fx_abs 0
scoreboard players set @s ss_fx_reg 0
scoreboard players set @s ss_fx_jump 0
scoreboard players set @s ss_fx_fire 0
scoreboard players set @s ss_fx_night 0

# Class cores, contribution measured in effect levels (not amplifier numbers).
execute if score @s ss_path matches 1 if score @s ss_t_van matches 1..3 run scoreboard players add @s ss_fx_str 1
execute if score @s ss_path matches 1 if score @s ss_t_van matches 4.. run scoreboard players add @s ss_fx_str 2
execute if score @s ss_path matches 2 if score @s ss_t_ward matches 1..3 run scoreboard players add @s ss_fx_res 1
execute if score @s ss_path matches 2 if score @s ss_t_ward matches 4.. run scoreboard players add @s ss_fx_res 2
execute if score @s ss_path matches 3 if score @s ss_t_path matches 1..3 run scoreboard players add @s ss_fx_spd 1
execute if score @s ss_path matches 3 if score @s ss_t_path matches 4.. run scoreboard players add @s ss_fx_spd 2
execute if score @s ss_path matches 4 if score @s ss_t_arc matches 1.. run scoreboard players add @s ss_fx_night 1
execute if score @s ss_path matches 4 if score @s ss_t_arc matches 4.. run scoreboard players add @s ss_fx_haste 1

# Mob-kill milestones are separate earned skill bonuses, so they stack with core/branch ranks.
execute if score @s ss_path matches 1 if score @s ss_dkills matches 10..39 run scoreboard players add @s ss_fx_str 1
execute if score @s ss_path matches 1 if score @s ss_dkills matches 40.. run scoreboard players add @s ss_fx_str 2
execute if score @s ss_path matches 2 if score @s ss_dkills matches 10..39 run scoreboard players add @s ss_fx_res 1
execute if score @s ss_path matches 2 if score @s ss_dkills matches 40.. run scoreboard players add @s ss_fx_res 2
execute if score @s ss_path matches 3 if score @s ss_dkills matches 10..39 run scoreboard players add @s ss_fx_spd 1
execute if score @s ss_path matches 3 if score @s ss_dkills matches 40.. run scoreboard players add @s ss_fx_spd 2
# Night Vision has no useful higher amplifier: keep it I with an extended duration.
execute if score @s ss_path matches 4 if score @s ss_dkills matches 10.. run scoreboard players set @s ss_fx_night 1

# Branch rank 3 grants a distinct level-I bonus; rank 5 opens its mastery purchase.
execute if score @s ss_path matches 1 if score @s ss_b11 matches 3.. run scoreboard players add @s ss_fx_str 1
execute if score @s ss_path matches 1 if score @s ss_b12 matches 3.. run scoreboard players add @s ss_fx_haste 1
execute if score @s ss_path matches 1 if score @s ss_b13 matches 3.. run scoreboard players add @s ss_fx_res 1
execute if score @s ss_path matches 2 if score @s ss_b21 matches 3.. run scoreboard players add @s ss_fx_res 1
execute if score @s ss_path matches 2 if score @s ss_b22 matches 3.. run scoreboard players add @s ss_fx_abs 1
execute if score @s ss_path matches 2 if score @s ss_b23 matches 3.. run scoreboard players add @s ss_fx_reg 1
execute if score @s ss_path matches 3 if score @s ss_b31 matches 3.. run scoreboard players add @s ss_fx_spd 1
execute if score @s ss_path matches 3 if score @s ss_b32 matches 3.. run scoreboard players add @s ss_fx_jump 1
execute if score @s ss_path matches 3 if score @s ss_b33 matches 3.. run scoreboard players set @s ss_fx_night 1
execute if score @s ss_path matches 4 if score @s ss_b41 matches 3.. run scoreboard players add @s ss_fx_fire 1
execute if score @s ss_path matches 4 if score @s ss_b42 matches 3.. run scoreboard players add @s ss_fx_haste 1
execute if score @s ss_path matches 4 if score @s ss_b43 matches 3.. run scoreboard players add @s ss_fx_reg 1

# NG+ perk levels add to skill bonuses, instead of competing with them via effect give.
execute if score @s ss_ng matches 1.. run function shatteredseals:mastery/passives

# Armour enchantments share the class/NG+ potion effect aggregator.
# Guardian Angel I + Guardian Angel I gives Absorption II, capped at V.
scoreboard players operation @s ss_fx_abs += @s ss_ench_abs
scoreboard players operation @s ss_fx_fire += @s ss_ench_fire
# Fire Resistance has no meaningful amplifier past I. Extra Phoenix Skin
# pieces add a modest absorption shield, making duplicate enchantments useful.
execute if score @s ss_ench_firepieces matches 2..3 run scoreboard players add @s ss_fx_abs 1
execute if score @s ss_ench_firepieces matches 4.. run scoreboard players add @s ss_fx_abs 2

# One potion application per effect prevents weaker sources overwriting a stacked level.
execute if score @s ss_fx_str matches 1 run effect give @s minecraft:strength 16 0 true
execute if score @s ss_fx_str matches 2 run effect give @s minecraft:strength 16 1 true
execute if score @s ss_fx_str matches 3 run effect give @s minecraft:strength 16 2 true
execute if score @s ss_fx_str matches 4 run effect give @s minecraft:strength 16 3 true
execute if score @s ss_fx_str matches 5.. run effect give @s minecraft:strength 16 4 true

execute if score @s ss_fx_res matches 1 run effect give @s minecraft:resistance 16 0 true
execute if score @s ss_fx_res matches 2 run effect give @s minecraft:resistance 16 1 true
execute if score @s ss_fx_res matches 3 run effect give @s minecraft:resistance 16 2 true
execute if score @s ss_fx_res matches 4 run effect give @s minecraft:resistance 16 3 true
execute if score @s ss_fx_res matches 5.. run effect give @s minecraft:resistance 16 4 true

execute if score @s ss_fx_spd matches 1 run effect give @s minecraft:speed 16 0 true
execute if score @s ss_fx_spd matches 2 run effect give @s minecraft:speed 16 1 true
execute if score @s ss_fx_spd matches 3 run effect give @s minecraft:speed 16 2 true
execute if score @s ss_fx_spd matches 4 run effect give @s minecraft:speed 16 3 true
execute if score @s ss_fx_spd matches 5.. run effect give @s minecraft:speed 16 4 true

execute if score @s ss_fx_haste matches 1 run effect give @s minecraft:haste 16 0 true
execute if score @s ss_fx_haste matches 2 run effect give @s minecraft:haste 16 1 true
execute if score @s ss_fx_haste matches 3 run effect give @s minecraft:haste 16 2 true
execute if score @s ss_fx_haste matches 4 run effect give @s minecraft:haste 16 3 true
execute if score @s ss_fx_haste matches 5.. run effect give @s minecraft:haste 16 4 true

execute if score @s ss_fx_abs matches 1 run effect give @s minecraft:absorption 16 0 true
execute if score @s ss_fx_abs matches 2 run effect give @s minecraft:absorption 16 1 true
execute if score @s ss_fx_abs matches 3 run effect give @s minecraft:absorption 16 2 true
execute if score @s ss_fx_abs matches 4 run effect give @s minecraft:absorption 16 3 true
execute if score @s ss_fx_abs matches 5.. run effect give @s minecraft:absorption 16 4 true

execute if score @s ss_fx_reg matches 1 run effect give @s minecraft:regeneration 16 0 true
execute if score @s ss_fx_reg matches 2 run effect give @s minecraft:regeneration 16 1 true
execute if score @s ss_fx_reg matches 3 run effect give @s minecraft:regeneration 16 2 true
execute if score @s ss_fx_reg matches 4 run effect give @s minecraft:regeneration 16 3 true
execute if score @s ss_fx_reg matches 5.. run effect give @s minecraft:regeneration 16 4 true

execute if score @s ss_fx_jump matches 1 run effect give @s minecraft:jump_boost 16 0 true
execute if score @s ss_fx_jump matches 2 run effect give @s minecraft:jump_boost 16 1 true
execute if score @s ss_fx_jump matches 3 run effect give @s minecraft:jump_boost 16 2 true
execute if score @s ss_fx_jump matches 4 run effect give @s minecraft:jump_boost 16 3 true
execute if score @s ss_fx_jump matches 5.. run effect give @s minecraft:jump_boost 16 4 true

execute if score @s ss_fx_fire matches 1.. run effect give @s minecraft:fire_resistance 16 0 true

execute if score @s ss_fx_night matches 1.. run effect give @s minecraft:night_vision 30 0 true
