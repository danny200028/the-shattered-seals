# Server-authoritative two-step Dragon Egg reset. Confirmation cannot be bypassed by a direct /trigger set 2.
execute if score @s ss_respec matches 1 run scoreboard players set @s ss_reset_confirm 1
execute if score @s ss_respec matches 1 run scoreboard players set @s ss_reset_timer 1200
execute if score @s ss_respec matches 1 run tellraw @s [{"text":"Reset all skill-tree allocations for one Dragon Egg? ","color":"yellow"},{"text":"[CONFIRM RESET]","color":"red","bold":true,"clickEvent":{"action":"run_command","value":"/trigger ss_respec set 2"},"hoverEvent":{"action":"show_text","contents":{"text":"Consume one Dragon Egg and refund normal and NG+ mastery points"}}}]
execute if score @s ss_respec matches 2 if score @s ss_reset_confirm matches 1 run function shatteredseals:progression/reset_confirm
execute if score @s ss_respec matches 2 unless score @s ss_reset_confirm matches 1 run tellraw @s {"text":"Request a skill reset first using /trigger ss_respec set 1.","color":"red"}
execute if score @s ss_respec matches 2 run scoreboard players set @s ss_reset_confirm 0
execute if score @s ss_respec matches 2 run scoreboard players set @s ss_reset_timer 0
# Only cancellation code 3 revokes pending consent without spending an egg.
execute if score @s ss_respec matches 3 run scoreboard players set @s ss_reset_confirm 0
execute if score @s ss_respec matches 3 run scoreboard players set @s ss_reset_timer 0
execute if score @s ss_respec matches 3 run tellraw @s {"text":"Skill reset cancelled. No Dragon Egg consumed.","color":"green"}
execute if score @s ss_respec matches 4.. run tellraw @s {"text":"Use /trigger ss_respec set 1 to review the reset first.","color":"yellow"}
