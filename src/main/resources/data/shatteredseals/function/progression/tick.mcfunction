execute as @a unless score @s ss_level matches 1.. run function shatteredseals:progression/init
execute as @a[scores={ss_level=1..}] unless score @s ss_points_v130 matches 1.. run function shatteredseals:progression/migrate_points
# Existing save migration: reset legacy mixed kill milestones/XP without touching earned skills.
execute as @a unless score @s ss_dkill_migrated matches 1.. run function shatteredseals:progression/migrate_dungeon_kills
scoreboard players enable @a ss_buy
scoreboard players enable @a ss_respec
execute as @a run function shatteredseals:progression/kill_xp
execute as @a[scores={ss_level=1..59}] if score @s ss_xp >= @s ss_need run function shatteredseals:progression/level_up
execute as @a[scores={ss_level=60..,ss_ng=1..,ss_master=..49}] if score @s ss_mxp >= @s ss_mneed run function shatteredseals:progression/master_up
execute as @a[scores={ss_buy=1..}] run function shatteredseals:progression/buy
execute as @a[scores={ss_respec=1..}] run function shatteredseals:progression/reset
scoreboard players set @a[scores={ss_buy=1..}] ss_buy 0
scoreboard players set @a[scores={ss_respec=1..}] ss_respec 0
execute if score #clock ss_clock matches 0 as @a run function shatteredseals:progression/passives

scoreboard players enable @a ss_branch
scoreboard players enable @a ss_ascend
execute as @a[scores={ss_branch=1..}] run function shatteredseals:talents/buy
execute as @a[scores={ss_ascend=1..}] run function shatteredseals:talents/master_buy
scoreboard players set @a[scores={ss_branch=1..}] ss_branch 0
scoreboard players set @a[scores={ss_ascend=1..}] ss_ascend 0

# NG+ mastery perk shop; passive levels are combined with class skills by progression/passives
scoreboard players enable @a ss_mperk
execute as @a[scores={ss_mperk=1..}] run function shatteredseals:mastery/shop
scoreboard players set @a[scores={ss_mperk=1..}] ss_mperk 0

# Chapter journal, dungeon locator and NG+ activation
scoreboard players enable @a ss_story
execute as @a[scores={ss_story=1}] run function shatteredseals:story/journal
execute as @a[scores={ss_story=2}] at @s run function shatteredseals:story/locate
execute as @a[scores={ss_story=3}] run function shatteredseals:ngplus/start
scoreboard players set @a[scores={ss_story=1..}] ss_story 0

# Ensure pre-v1.0.44 worlds have a score entry for mastery purchase conditions.
scoreboard players add @a ss_mp_guard 0
scoreboard players add @a ss_mp_haste 0
scoreboard players add @a ss_mp_fury 0
scoreboard players add @a ss_reset_confirm 0
# Clear stale skill-reset consent automatically after 60 seconds.
scoreboard players add @a ss_reset_timer 0
execute as @a[scores={ss_reset_timer=1..}] run scoreboard players remove @s ss_reset_timer 1
execute as @a[scores={ss_reset_timer=0,ss_reset_confirm=1..}] run tellraw @s {"text":"Your Dragon Egg reset confirmation has expired.","color":"yellow"}
scoreboard players set @a[scores={ss_reset_timer=0,ss_reset_confirm=1..}] ss_reset_confirm 0
