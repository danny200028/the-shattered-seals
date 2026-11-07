# Server validates cost; resource is spent only on successful dispatch.
execute if score @s ss_path matches 1 if score @s ss_fury matches 45.. run function shatteredseals:combat/vanguard_4
execute if score @s ss_path matches 1 if score @s ss_fury matches ..44 run title @s actionbar {"text":"Not enough FURY (45 needed)","color":"red"}
execute if score @s ss_path matches 2 if score @s ss_guard matches 45.. run function shatteredseals:combat/warden_4
execute if score @s ss_path matches 2 if score @s ss_guard matches ..44 run title @s actionbar {"text":"Not enough GUARD (45 needed)","color":"red"}
execute if score @s ss_path matches 3 if score @s ss_focus matches 45.. run function shatteredseals:combat/pathfinder_4
execute if score @s ss_path matches 3 if score @s ss_focus matches ..44 run title @s actionbar {"text":"Not enough FOCUS (45 needed)","color":"red"}
execute if score @s ss_path matches 4 if score @s ss_arcane matches 45.. run function shatteredseals:combat/arcanist_4
execute if score @s ss_path matches 4 if score @s ss_arcane matches ..44 run title @s actionbar {"text":"Not enough ARCANE (45 needed)","color":"red"}
