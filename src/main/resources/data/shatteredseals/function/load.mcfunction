# Datapack rules are installed in the mod JAR and run in any loaded world.
scoreboard objectives add ss_bt dummy
scoreboard objectives add ss_hp dummy
scoreboard objectives add ss_clock dummy
scoreboard objectives add ss_path dummy
scoreboard objectives add ss_cd dummy
scoreboard objectives add ss_kills minecraft.custom:minecraft.mob_kills
scoreboard objectives add ss_choose trigger
scoreboard objectives add ss_cast trigger
scoreboard objectives add ss_won dummy
scoreboard objectives add ss_ng dummy
scoreboard players set #clock ss_clock 0
bossbar add shatteredseals:smuggler_king {"text":"Smuggler King","color":"gold","bold":true}
bossbar set shatteredseals:smuggler_king max 150
bossbar set shatteredseals:smuggler_king color purple
bossbar set shatteredseals:smuggler_king players
bossbar add shatteredseals:cinder_warden {"text":"Cinder Warden","color":"red","bold":true}
bossbar set shatteredseals:cinder_warden max 170
bossbar set shatteredseals:cinder_warden color purple
bossbar set shatteredseals:cinder_warden players
bossbar add shatteredseals:hollow_knight {"text":"Hollow Knight","color":"dark_purple","bold":true}
bossbar set shatteredseals:hollow_knight max 240
bossbar set shatteredseals:hollow_knight color purple
bossbar set shatteredseals:hollow_knight players
bossbar add shatteredseals:arcane_seer {"text":"Arcane Seer","color":"aqua","bold":true}
bossbar set shatteredseals:arcane_seer max 180
bossbar set shatteredseals:arcane_seer color purple
bossbar set shatteredseals:arcane_seer players
bossbar add shatteredseals:sixfold_guardian {"text":"Sixfold Guardian","color":"yellow","bold":true}
bossbar set shatteredseals:sixfold_guardian max 310
bossbar set shatteredseals:sixfold_guardian color purple
bossbar set shatteredseals:sixfold_guardian players
bossbar add shatteredseals:hollow_king {"text":"Hollow King","color":"dark_red","bold":true}
bossbar set shatteredseals:hollow_king max 450
bossbar set shatteredseals:hollow_king color red
bossbar set shatteredseals:hollow_king players
scoreboard objectives add ss_upgrade trigger
scoreboard objectives add ss_upok dummy
scoreboard objectives add ss_mat dummy
scoreboard objectives add ss_metal dummy
scoreboard objectives add ss_ritual minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add ss_rank dummy
scoreboard objectives add ss_status trigger
scoreboard objectives add ss_vk_smug dummy
scoreboard objectives add ss_vk_cind dummy
scoreboard objectives add ss_vk_holl dummy
scoreboard objectives add ss_vk_arca dummy
scoreboard objectives add ss_vk_six dummy
scoreboard objectives add ss_vk_king dummy
scoreboard objectives add ss_ability_cd dummy
scoreboard objectives add ss_useguard dummy

scoreboard objectives add ss_menu trigger

scoreboard objectives add ss_level dummy
scoreboard objectives add ss_xp dummy
scoreboard objectives add ss_need dummy
scoreboard objectives add ss_points dummy
scoreboard objectives add ss_spent dummy
scoreboard objectives add ss_master dummy
scoreboard objectives add ss_mxp dummy
scoreboard objectives add ss_mneed dummy
scoreboard objectives add ss_mpoints dummy
scoreboard objectives add ss_kprev dummy
scoreboard objectives add ss_delta dummy
scoreboard objectives add ss_buy trigger
scoreboard objectives add ss_respec trigger
scoreboard objectives add ss_egg dummy
scoreboard objectives add ss_dragon dummy
scoreboard objectives add ss_talent dummy
scoreboard objectives add ss_t_van dummy
scoreboard objectives add ss_t_ward dummy
scoreboard objectives add ss_t_path dummy
scoreboard objectives add ss_t_arc dummy
# RPG HUD
scoreboard objectives add ss_hud trigger
scoreboard objectives add ss_hudon dummy
scoreboard objectives add ss_hudinit dummy
scoreboard objectives add ss_hudclock dummy

# 1.0.25 XP bar percentage (0-10 segments)
scoreboard objectives add ss_hudfill dummy
scoreboard objectives add ss_hudtmp dummy

# v1.0.26 HUD style preference
scoreboard objectives add ss_hudstyle trigger
scoreboard objectives add ss_hudmode dummy

scoreboard objectives add ss_hudclick minecraft.used:minecraft.warped_fungus_on_a_stick
scoreboard objectives add ss_hudlast dummy
scoreboard objectives add ss_hudchoice trigger

scoreboard objectives add ss_branch trigger
scoreboard objectives add ss_ascend trigger
scoreboard objectives add ss_b11 dummy
scoreboard objectives add ss_a11 dummy
scoreboard objectives add ss_b12 dummy
scoreboard objectives add ss_a12 dummy
scoreboard objectives add ss_b13 dummy
scoreboard objectives add ss_a13 dummy
scoreboard objectives add ss_b21 dummy
scoreboard objectives add ss_a21 dummy
scoreboard objectives add ss_b22 dummy
scoreboard objectives add ss_a22 dummy
scoreboard objectives add ss_b23 dummy
scoreboard objectives add ss_a23 dummy
scoreboard objectives add ss_b31 dummy
scoreboard objectives add ss_a31 dummy
scoreboard objectives add ss_b32 dummy
scoreboard objectives add ss_a32 dummy
scoreboard objectives add ss_b33 dummy
scoreboard objectives add ss_a33 dummy
scoreboard objectives add ss_b41 dummy
scoreboard objectives add ss_a41 dummy
scoreboard objectives add ss_b42 dummy
scoreboard objectives add ss_a42 dummy
scoreboard objectives add ss_b43 dummy
scoreboard objectives add ss_a43 dummy

scoreboard objectives add ss_switch trigger
scoreboard objectives add ss_classconfirm trigger
scoreboard objectives add ss_switchcd dummy
scoreboard objectives add ss_pending dummy

scoreboard objectives add ss_points_v130 dummy
scoreboard objectives add ss_migration dummy
scoreboard players set #two ss_migration 2

# 1.0.32 active combat ability
scoreboard objectives add ss_burst trigger
scoreboard objectives add ss_burst_cd dummy

# 1.0.33: independent ability cooldowns, slots 2-4
scoreboard objectives add ss_burst_cd2 dummy
scoreboard objectives add ss_burst_cd3 dummy
scoreboard objectives add ss_burst_cd4 dummy

# 1.0.35 persistent class resource meters
scoreboard objectives add ss_fury dummy
scoreboard objectives add ss_guard dummy
scoreboard objectives add ss_focus dummy
scoreboard objectives add ss_arcane dummy
scoreboard objectives add ss_resinit dummy
scoreboard objectives add ss_resource_clock dummy
scoreboard objectives add ss_damage_now minecraft.custom:minecraft.damage_dealt
scoreboard objectives add ss_damage_prev dummy
scoreboard objectives add ss_taken_now minecraft.custom:minecraft.damage_taken
scoreboard objectives add ss_taken_prev dummy
scoreboard objectives add ss_damage_delta dummy
scoreboard objectives add ss_taken_delta dummy

# 1.0.36 combat balance and validation
scoreboard objectives add ss_sprint_now minecraft.custom:minecraft.sprint_one_cm
scoreboard objectives add ss_sprint_prev dummy
scoreboard objectives add ss_sprint_delta dummy
scoreboard objectives add ss_combat_test trigger

scoreboard objectives add ss_mperk trigger
scoreboard objectives add ss_mp_guard dummy
scoreboard objectives add ss_mp_haste dummy
scoreboard objectives add ss_mp_fury dummy

# Main story: survival-accessible quest journal and dungeon locator
scoreboard objectives add ss_story trigger
