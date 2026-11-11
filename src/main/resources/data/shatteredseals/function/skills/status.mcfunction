tellraw @s {"text":"THE SHATTERED SEALS | Skill Paths","color":"gold","italic":false}
tellraw @s [{"text":"Hostile mobs defeated: ","color":"yellow"},{"score":{"name":"@s","objective":"ss_dkills"}}]
tellraw @s [{"text":"Skill mastery (0-3): ","color":"aqua"},{"score":{"name":"@s","objective":"ss_rank"}}]
tellraw @s [{"text":"Current class (1 Vanguard, 2 Warden, 3 Pathfinder, 4 Arcanist): ","color":"green"},{"score":{"name":"@s","objective":"ss_path"}}]
tellraw @s {"text":"Hostile milestones: 10 kills = Adept; 40 = Champion; 100 = Master.","color":"gray","italic":false}
tellraw @s {"text":"Choose: /trigger ss_choose set 1-4 | Cast: /trigger ss_cast set 1","color":"aqua","italic":false}
tellraw @s {"text":"Relic upgrades: /trigger ss_upgrade set 1 (hold relic)","color":"light_purple","italic":false}
