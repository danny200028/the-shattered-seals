# Server validates cost; resource is spent only on successful dispatch.
execute if score @s ss_path matches 1 if score @s ss_fury matches 28.. run function shatteredseals:combat/vanguard_3
execute if score @s ss_path matches 1 if score @s ss_fury matches ..27 run title @s actionbar {"text":"Not enough FURY (28 needed)","color":"red"}
execute if score @s ss_path matches 2 if score @s ss_guard matches 28.. run function shatteredseals:combat/warden_3
execute if score @s ss_path matches 2 if score @s ss_guard matches ..27 run title @s actionbar {"text":"Not enough GUARD (28 needed)","color":"red"}
execute if score @s ss_path matches 3 if score @s ss_focus matches 28.. run function shatteredseals:combat/pathfinder_3
execute if score @s ss_path matches 3 if score @s ss_focus matches ..27 run title @s actionbar {"text":"Not enough FOCUS (28 needed)","color":"red"}
execute if score @s ss_path matches 4 if score @s ss_arcane matches 28.. run function shatteredseals:combat/arcanist_3
execute if score @s ss_path matches 4 if score @s ss_arcane matches ..27 run title @s actionbar {"text":"Not enough ARCANE (28 needed)","color":"red"}
