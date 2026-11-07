# v1.0.38: explicit two-step confirmation; only option 2 consumes the egg.
execute if score @s ss_respec matches 1 run tellraw @s [{"text":"Reset all skill-tree allocations for one Dragon Egg? ","color":"yellow"},{"text":"[CONFIRM RESET]","color":"red","bold":true,"clickEvent":{"action":"run_command","value":"/trigger ss_respec set 2"},"hoverEvent":{"action":"show_text","value":"Consume one Dragon Egg and refund spent skill points"}}]
execute if score @s ss_respec matches 2 run function shatteredseals:progression/reset_confirm
execute if score @s ss_respec matches 3.. run tellraw @s {"text":"Use /trigger ss_respec set 1 to review the reset first.","color":"yellow"}
