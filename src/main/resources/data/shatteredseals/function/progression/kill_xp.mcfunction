# XP from all player-defeated hostile mobs; only dungeon kills advance milestones.
scoreboard players operation @s ss_delta = @s ss_hkills
scoreboard players operation @s ss_delta -= @s ss_hprev
execute if score @s ss_delta matches 1.. run function shatteredseals:progression/award_kills
scoreboard players operation @s ss_hprev = @s ss_hkills
