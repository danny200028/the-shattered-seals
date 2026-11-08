scoreboard players add #clock ss_clock 1
execute if score #clock ss_clock matches 80.. run scoreboard players set #clock ss_clock 0
scoreboard players add @a ss_path 0
scoreboard players add @a ss_cd 0
scoreboard players add @a ss_won 0
scoreboard players add @a ss_ng 0
scoreboard players add @a ss_ngchapter 0
# Existing New Game+ players start their first guided replay.
execute as @a[scores={ss_ng=1..,ss_ngchapter=0}] run scoreboard players set @s ss_ngchapter 1
scoreboard players enable @a ss_choose
scoreboard players enable @a ss_cast
execute as @a[tag=!ss_welcome] run function shatteredseals:welcome
execute as @a[scores={ss_cd=1..}] run scoreboard players remove @s ss_cd 1
execute as @a[scores={ss_choose=1,ss_path=0}] run scoreboard players set @s ss_path 1
execute as @a[scores={ss_choose=1,ss_path=1}] run tellraw @s {"text":"You have chosen Vanguard.","color":"gold","bold":false}
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=1,ss_kills=10..}] run effect give @s minecraft:strength 5 0 true
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=1,ss_kills=40..}] run effect give @s minecraft:strength 5 1 true
execute as @a[scores={ss_cast=1,ss_cd=0,ss_path=1}] at @s run function shatteredseals:skills/cast_vanguard
execute as @a[scores={ss_choose=2,ss_path=0}] run scoreboard players set @s ss_path 2
execute as @a[scores={ss_choose=2,ss_path=2}] run tellraw @s {"text":"You have chosen Warden.","color":"gold","bold":false}
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=2,ss_kills=10..}] run effect give @s minecraft:resistance 5 0 true
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=2,ss_kills=40..}] run effect give @s minecraft:resistance 5 1 true
execute as @a[scores={ss_cast=1,ss_cd=0,ss_path=2}] at @s run function shatteredseals:skills/cast_warden
execute as @a[scores={ss_choose=3,ss_path=0}] run scoreboard players set @s ss_path 3
execute as @a[scores={ss_choose=3,ss_path=3}] run tellraw @s {"text":"You have chosen Pathfinder.","color":"gold","bold":false}
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=3,ss_kills=10..}] run effect give @s minecraft:speed 5 0 true
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=3,ss_kills=40..}] run effect give @s minecraft:speed 5 1 true
execute as @a[scores={ss_cast=1,ss_cd=0,ss_path=3}] at @s run function shatteredseals:skills/cast_pathfinder
execute as @a[scores={ss_choose=4,ss_path=0}] run scoreboard players set @s ss_path 4
execute as @a[scores={ss_choose=4,ss_path=4}] run tellraw @s {"text":"You have chosen Arcanist.","color":"gold","bold":false}
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=4,ss_kills=10..}] run effect give @s minecraft:night_vision 5 0 true
execute if score #clock ss_clock matches 0 as @a[scores={ss_path=4,ss_kills=40..}] run effect give @s minecraft:night_vision 5 1 true
execute as @a[scores={ss_cast=1,ss_cd=0,ss_path=4}] at @s run function shatteredseals:skills/cast_arcanist
execute as @a[scores={ss_choose=1..}] run scoreboard players set @s ss_choose 0
execute as @a[scores={ss_cast=1..}] run scoreboard players set @s ss_cast 0
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"emberbound"}] run effect give @s minecraft:fire_resistance 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"emberbound"}] run effect give @s minecraft:fire_resistance 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"kings_fang"}] run effect give @s minecraft:strength 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"kings_fang"}] run effect give @s minecraft:strength 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"gravekeeper"}] run effect give @s minecraft:regeneration 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"gravekeeper"}] run effect give @s minecraft:regeneration 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"starfall"}] run effect give @s minecraft:slow_falling 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"starfall"}] run effect give @s minecraft:slow_falling 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"wardens_oath"}] run effect give @s minecraft:resistance 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"wardens_oath"}] run effect give @s minecraft:resistance 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:diamond_sword[minecraft:custom_data~{ss_weapon:"kings_bane"}] run effect give @s minecraft:haste 5 0 true
execute if score #clock ss_clock matches 0 as @a if items entity @s weapon.mainhand minecraft:netherite_sword[minecraft:custom_data~{ss_weapon:"kings_bane"}] run effect give @s minecraft:haste 5 0 true
scoreboard players enable @a ss_upgrade
scoreboard players enable @a ss_status
scoreboard players add @a ss_rank 0
execute if score #clock ss_clock matches 0 as @a[scores={ss_kills=10..}] run scoreboard players set @s ss_rank 1
execute if score #clock ss_clock matches 0 as @a[scores={ss_kills=40..}] run scoreboard players set @s ss_rank 2
execute if score #clock ss_clock matches 0 as @a[scores={ss_kills=100..}] run scoreboard players set @s ss_rank 3
execute as @a[scores={ss_upgrade=1..}] run function shatteredseals:upgrade/attempt
scoreboard players set @a[scores={ss_upgrade=1..}] ss_upgrade 0
execute as @a[scores={ss_status=1..}] run function shatteredseals:skills/status
scoreboard players set @a[scores={ss_status=1..}] ss_status 0
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"smuggler_king"}] run function shatteredseals:ritual/use_smuggler_king
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"cinder_warden"}] run function shatteredseals:ritual/use_cinder_warden
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"hollow_knight"}] run function shatteredseals:ritual/use_hollow_knight
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"arcane_seer"}] run function shatteredseals:ritual/use_arcane_seer
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"sixfold_guardian"}] run function shatteredseals:ritual/use_sixfold_guardian
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"hollow_king"}] run function shatteredseals:ritual/use_hollow_king
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"ember_guard"}] run function shatteredseals:ability/ember_guard
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"rift_step"}] run function shatteredseals:ability/rift_step
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"echo_ping"}] run function shatteredseals:ability/echo_ping
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"wardens_pulse"}] run function shatteredseals:ability/wardens_pulse
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"frost_snare"}] run function shatteredseals:ability/frost_snare
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"delvers_draft"}] run function shatteredseals:ability/delvers_draft
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"soul_mend"}] run function shatteredseals:ability/soul_mend
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"storm_call"}] run function shatteredseals:ability/storm_call
execute as @a[scores={ss_ritual=1..}] at @s if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ability:"shadow_cloak"}] run function shatteredseals:ability/shadow_cloak
scoreboard players set @a[scores={ss_ritual=1..}] ss_ritual 0
scoreboard players add @a ss_vk_smug 0
scoreboard players add @a ss_vk_cind 0
scoreboard players add @a ss_vk_holl 0
scoreboard players add @a ss_vk_arca 0
scoreboard players add @a ss_vk_six 0
scoreboard players add @a ss_vk_king 0
scoreboard players add @a ss_ability_cd 0
scoreboard players add @a ss_useguard 0
execute as @a[scores={ss_ability_cd=1..}] run scoreboard players remove @s ss_ability_cd 1

# 1.0.5: keep boss reward tracking independent of advancement credit.
function shatteredseals:reward/watch_tick
# Reap orphaned boss adds after victory and restore old saves.
function shatteredseals:boss/minion_cleanup

# Interactive chat menus (safe for single-player, no cheats required after load)
scoreboard players enable @a ss_menu
execute as @a[scores={ss_menu=1}] run function shatteredseals:ui/skills_menu
execute as @a[scores={ss_menu=2}] run function shatteredseals:ui/arsenal_menu
execute as @a[scores={ss_menu=3}] run function shatteredseals:ui/forge_menu
execute as @a[scores={ss_menu=1..}] run scoreboard players set @s ss_menu 0

# Shattered Seals elite auras, triggered once every 4 seconds
execute if score #clock ss_clock matches 0 run function shatteredseals:elites/pulse

# Character progression
function shatteredseals:progression/tick

# Per-player RPG HUD, refreshed every second
function shatteredseals:hud/tick

# 1.0.29 class switch control
scoreboard players enable @a ss_switch
execute as @a[scores={ss_switch=1..4}] run function shatteredseals:classes/request
scoreboard players set @a[scores={ss_switch=1..}] ss_switch 0

# 1.0.35: update resource meters before processing casts.
execute as @a run function shatteredseals:combat/resource_tick

# 1.0.32: server-authoritative combat burst on Q. Existing cast ability remains unchanged.
scoreboard players add @a ss_burst_cd 0
scoreboard players enable @a ss_burst
scoreboard players enable @a ss_combat_test
execute as @a[scores={ss_burst_cd=1..}] run scoreboard players remove @s ss_burst_cd 1
execute as @a[scores={ss_burst=1,ss_burst_cd=0}] at @s run function shatteredseals:combat/dispatch
# Cooldown feedback is now handled by the native HUD.
scoreboard players add @a ss_burst_cd2 0
execute as @a[scores={ss_burst_cd2=1..}] run scoreboard players remove @s ss_burst_cd2 1
execute as @a[scores={ss_burst=2,ss_burst_cd2=0}] at @s run function shatteredseals:combat/dispatch_2
# Cooldown feedback is now handled by the native HUD.
scoreboard players add @a ss_burst_cd3 0
execute as @a[scores={ss_burst_cd3=1..}] run scoreboard players remove @s ss_burst_cd3 1
execute as @a[scores={ss_burst=3,ss_burst_cd3=0}] at @s run function shatteredseals:combat/dispatch_3
# Cooldown feedback is now handled by the native HUD.
scoreboard players add @a ss_burst_cd4 0
execute as @a[scores={ss_burst_cd4=1..}] run scoreboard players remove @s ss_burst_cd4 1
execute as @a[scores={ss_burst=4,ss_burst_cd4=0}] at @s run function shatteredseals:combat/dispatch_4
# Cooldown feedback is now handled by the native HUD.
scoreboard players set @a[scores={ss_burst=1..}] ss_burst 0
# Player-accessible combat diagnostic: /trigger ss_combat_test set 1
execute as @a[scores={ss_combat_test=1..}] run function shatteredseals:combat/test_status
scoreboard players set @a[scores={ss_combat_test=1..}] ss_combat_test 0

# 1.8.9-style full-strength clicks: short recharge rather than post-1.9 timed sword cooldown.
# Refresh every four seconds to persist through death, dimension changes and equipment swaps.
execute if score #clock ss_clock matches 0 as @a run attribute @s minecraft:generic.attack_speed base set 64

# v1.0.53: no-cheats vanilla-XP enchantment book merchant
scoreboard players enable @a ss_ebook
execute as @a[scores={ss_ebook=1..}] run function shatteredseals:enchantments/shop
