tag @s add ss_welcome
tellraw @s {"text":"THE SHATTERED SEALS | Gameplay mod loaded!","color":"gold","bold":true}
tellraw @s {"text":"Choose a class below. Abilities unlock with the number of monsters you have defeated.","color":"yellow","bold":false}
tellraw @s {"text":"  [Vanguard]","color":"red","clickEvent":{"action":"run_command","value":"/trigger ss_choose set 1"},"hoverEvent":{"action":"show_text","contents":"Click to run this command"}}
tellraw @s {"text":"  [Warden]","color":"blue","clickEvent":{"action":"run_command","value":"/trigger ss_choose set 2"},"hoverEvent":{"action":"show_text","contents":"Click to run this command"}}
tellraw @s {"text":"  [Pathfinder]","color":"green","clickEvent":{"action":"run_command","value":"/trigger ss_choose set 3"},"hoverEvent":{"action":"show_text","contents":"Click to run this command"}}
tellraw @s {"text":"  [Arcanist]","color":"light_purple","clickEvent":{"action":"run_command","value":"/trigger ss_choose set 4"},"hoverEvent":{"action":"show_text","contents":"Click to run this command"}}
tellraw @s {"text":"  [Use your active skill]","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_cast set 1"},"hoverEvent":{"action":"show_text","contents":"Click to run this command"}}
tellraw @s {"text":"Boss summoning, Sanctuary construction and realm travel use /function commands with cheats enabled.","color":"gray","bold":false}
tellraw @s {"text":"See INSTALL_AND_COMMANDS.txt inside the modpack for commands.","color":"gray","bold":false}
tellraw @s {"text":"NEW: Upgrade your Dormant Warden Relics in Survival with /trigger ss_upgrade set 1.","color":"gold","italic":false}
tellraw @s {"text":"NEW: Craft a boss Summoning Sigil and right-click to start the encounter!","color":"green","italic":false}
tellraw @s {"text":"Use /trigger ss_status set 1 to track four-path skill mastery.","color":"aqua","italic":false}
