tellraw @s [{"text":"Level ","color":"gold"},{"score":{"name":"@s","objective":"ss_level"}},{"text":" | XP ","color":"yellow"},{"score":{"name":"@s","objective":"ss_xp"}},{"text":" / "},{"score":{"name":"@s","objective":"ss_need"}}]
tellraw @s [{"text":"Available skill points: ","color":"aqua"},{"score":{"name":"@s","objective":"ss_points"}},{"text":" | Mastery: "},{"score":{"name":"@s","objective":"ss_master"}},{"text":" | Mastery points: "},{"score":{"name":"@s","objective":"ss_mpoints"}}]
tellraw @s {"text":"Spend points: /trigger ss_buy set 1 Vanguard, 2 Warden, 3 Pathfinder, 4 Arcanist. Each talent has 5 ranks.","color":"gray"}
tellraw @s {"text":"Respec: /trigger ss_respec set 1 (cost: Dragon Egg)","color":"light_purple"}

tellraw @s {"text":"Branch mastery: 8 points, level 50+, 100 dungeon kills, and rank 5 in that branch.","color":"light_purple"}
tellraw @s {"text":"3 skill points per level. All class upgrades persist when switching. Dragon Egg resets all trees.","color":"aqua"}
