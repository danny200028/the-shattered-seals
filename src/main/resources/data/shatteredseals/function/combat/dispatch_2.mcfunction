# Server validates cost; resource is spent only on successful dispatch.
execute if score @s ss_path matches 1 if score @s ss_fury matches 20.. run function shatteredseals:combat/vanguard_2
execute if score @s ss_path matches 1 if score @s ss_fury matches ..19 run title @s actionbar {"text":"Not enough FURY (20 needed)","color":"red"}
execute if score @s ss_path matches 2 if score @s ss_guard matches 20.. run function shatteredseals:combat/warden_2
execute if score @s ss_path matches 2 if score @s ss_guard matches ..19 run title @s actionbar {"text":"Not enough GUARD (20 needed)","color":"red"}
execute if score @s ss_path matches 3 if score @s ss_focus matches 20.. run function shatteredseals:combat/pathfinder_2
execute if score @s ss_path matches 3 if score @s ss_focus matches ..19 run title @s actionbar {"text":"Not enough FOCUS (20 needed)","color":"red"}
execute if score @s ss_path matches 4 if score @s ss_arcane matches 20.. run function shatteredseals:combat/arcanist_2
execute if score @s ss_path matches 4 if score @s ss_arcane matches ..19 run title @s actionbar {"text":"Not enough ARCANE (20 needed)","color":"red"}
