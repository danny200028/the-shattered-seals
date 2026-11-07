# Server validates cost; resource is spent only on successful dispatch.
execute if score @s ss_path matches 1 if score @s ss_fury matches 12.. run function shatteredseals:combat/vanguard
execute if score @s ss_path matches 1 if score @s ss_fury matches ..11 run title @s actionbar {"text":"Not enough FURY (12 needed)","color":"red"}
execute if score @s ss_path matches 2 if score @s ss_guard matches 12.. run function shatteredseals:combat/warden
execute if score @s ss_path matches 2 if score @s ss_guard matches ..11 run title @s actionbar {"text":"Not enough GUARD (12 needed)","color":"red"}
execute if score @s ss_path matches 3 if score @s ss_focus matches 12.. run function shatteredseals:combat/pathfinder
execute if score @s ss_path matches 3 if score @s ss_focus matches ..11 run title @s actionbar {"text":"Not enough FOCUS (12 needed)","color":"red"}
execute if score @s ss_path matches 4 if score @s ss_arcane matches 12.. run function shatteredseals:combat/arcanist
execute if score @s ss_path matches 4 if score @s ss_arcane matches ..11 run title @s actionbar {"text":"Not enough ARCANE (12 needed)","color":"red"}
