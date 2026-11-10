# This loop starts after summon and remains active while any boss exists.
execute as @e[tag=ss_boss] run scoreboard players add @s ss_bt 1
# A short sword combo supplements the Smuggler King's normal crossbow AI.
# The attack is distance-gated, cooldown-limited and never hits spectators.
execute as @e[tag=ss_smuggler_king] run scoreboard players add @s ss_melee 1
execute as @e[tag=ss_smuggler_king,scores={ss_melee=30..}] at @s if entity @p[distance=..2.6,gamemode=!creative,gamemode=!spectator] run function shatteredseals:boss/smuggler_king/melee_strike
execute as @e[tag=ss_smuggler_king,scores={ss_melee=8}] run item replace entity @s weapon.mainhand with minecraft:crossbow
execute as @e[tag=ss_boss] store result score @s ss_hp run data get entity @s Health 1
execute as @e[tag=ss_smuggler_king,tag=!ss_phase2] at @s if score @s ss_hp matches ..75 run function shatteredseals:boss/smuggler_king/phase_two
execute as @e[tag=ss_smuggler_king,tag=!ss_phase3] at @s if score @s ss_hp matches ..50 run function shatteredseals:boss/smuggler_king/phase_three
execute as @e[tag=ss_smuggler_king,scores={ss_bt=100..}] at @s run function shatteredseals:boss/smuggler_king/pulse
execute if entity @e[tag=ss_smuggler_king,limit=1] store result bossbar shatteredseals:smuggler_king value run data get entity @e[tag=ss_smuggler_king,limit=1] Health 1
execute at @e[tag=ss_smuggler_king,limit=1] run bossbar set shatteredseals:smuggler_king players @a[distance=..48]
execute unless entity @e[tag=ss_smuggler_king,limit=1] run bossbar set shatteredseals:smuggler_king players
execute as @e[tag=ss_cinder_warden,tag=!ss_phase2] at @s if score @s ss_hp matches ..85 run function shatteredseals:boss/cinder_warden/phase_two
execute as @e[tag=ss_cinder_warden,tag=!ss_phase3] at @s if score @s ss_hp matches ..56 run function shatteredseals:boss/cinder_warden/phase_three
execute as @e[tag=ss_cinder_warden,scores={ss_bt=100..}] at @s run function shatteredseals:boss/cinder_warden/pulse
execute if entity @e[tag=ss_cinder_warden,limit=1] store result bossbar shatteredseals:cinder_warden value run data get entity @e[tag=ss_cinder_warden,limit=1] Health 1
execute at @e[tag=ss_cinder_warden,limit=1] run bossbar set shatteredseals:cinder_warden players @a[distance=..48]
execute unless entity @e[tag=ss_cinder_warden,limit=1] run bossbar set shatteredseals:cinder_warden players
execute as @e[tag=ss_hollow_knight,tag=!ss_phase2] at @s if score @s ss_hp matches ..120 run function shatteredseals:boss/hollow_knight/phase_two
execute as @e[tag=ss_hollow_knight,tag=!ss_phase3] at @s if score @s ss_hp matches ..80 run function shatteredseals:boss/hollow_knight/phase_three
execute as @e[tag=ss_hollow_knight,scores={ss_bt=100..}] at @s run function shatteredseals:boss/hollow_knight/pulse
execute if entity @e[tag=ss_hollow_knight,limit=1] store result bossbar shatteredseals:hollow_knight value run data get entity @e[tag=ss_hollow_knight,limit=1] Health 1
execute at @e[tag=ss_hollow_knight,limit=1] run bossbar set shatteredseals:hollow_knight players @a[distance=..48]
execute unless entity @e[tag=ss_hollow_knight,limit=1] run bossbar set shatteredseals:hollow_knight players
execute as @e[tag=ss_arcane_seer,tag=!ss_phase2] at @s if score @s ss_hp matches ..90 run function shatteredseals:boss/arcane_seer/phase_two
execute as @e[tag=ss_arcane_seer,tag=!ss_phase3] at @s if score @s ss_hp matches ..60 run function shatteredseals:boss/arcane_seer/phase_three
execute as @e[tag=ss_arcane_seer,scores={ss_bt=100..}] at @s run function shatteredseals:boss/arcane_seer/pulse
execute if entity @e[tag=ss_arcane_seer,limit=1] store result bossbar shatteredseals:arcane_seer value run data get entity @e[tag=ss_arcane_seer,limit=1] Health 1
execute at @e[tag=ss_arcane_seer,limit=1] run bossbar set shatteredseals:arcane_seer players @a[distance=..48]
execute unless entity @e[tag=ss_arcane_seer,limit=1] run bossbar set shatteredseals:arcane_seer players
execute as @e[tag=ss_sixfold_guardian,tag=!ss_phase2] at @s if score @s ss_hp matches ..155 run function shatteredseals:boss/sixfold_guardian/phase_two
execute as @e[tag=ss_sixfold_guardian,tag=!ss_phase3] at @s if score @s ss_hp matches ..103 run function shatteredseals:boss/sixfold_guardian/phase_three
execute as @e[tag=ss_sixfold_guardian,scores={ss_bt=100..}] at @s run function shatteredseals:boss/sixfold_guardian/pulse
execute if entity @e[tag=ss_sixfold_guardian,limit=1] store result bossbar shatteredseals:sixfold_guardian value run data get entity @e[tag=ss_sixfold_guardian,limit=1] Health 1
execute at @e[tag=ss_sixfold_guardian,limit=1] run bossbar set shatteredseals:sixfold_guardian players @a[distance=..48]
execute unless entity @e[tag=ss_sixfold_guardian,limit=1] run bossbar set shatteredseals:sixfold_guardian players
execute as @e[tag=ss_hollow_king,tag=!ss_phase2] at @s if score @s ss_hp matches ..225 run function shatteredseals:boss/hollow_king/phase_two
execute as @e[tag=ss_hollow_king,tag=!ss_phase3] at @s if score @s ss_hp matches ..150 run function shatteredseals:boss/hollow_king/phase_three
execute as @e[tag=ss_hollow_king,scores={ss_bt=100..}] at @s run function shatteredseals:boss/hollow_king/pulse
execute if entity @e[tag=ss_hollow_king,limit=1] store result bossbar shatteredseals:hollow_king value run data get entity @e[tag=ss_hollow_king,limit=1] Health 1
execute at @e[tag=ss_hollow_king,limit=1] run bossbar set shatteredseals:hollow_king players @a[distance=..48]
execute unless entity @e[tag=ss_hollow_king,limit=1] run bossbar set shatteredseals:hollow_king players
execute if entity @e[tag=ss_boss,limit=1] run schedule function shatteredseals:boss/heartbeat 1t replace
