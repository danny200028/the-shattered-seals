execute align xyz positioned ~3.5 ~ ~0.5 unless predicate shatteredseals:arena/smugglers_den run return 0
execute align xyz positioned ~3.5 ~ ~0.5 unless block ~ ~ ~ minecraft:air run return run tellraw @s {"text":"Clear the ritual pad's east side to make room for the boss.","color":"red"}
execute align xyz positioned ~3.5 ~ ~0.5 unless block ~ ~1 ~ minecraft:air run return run tellraw @s {"text":"Clear the ritual pad's east side to make room for the boss.","color":"red"}
execute align xyz positioned ~3.5 ~ ~0.5 if block ~ ~-1 ~ minecraft:air run return run tellraw @s {"text":"Restore the floor east of the ritual pad.","color":"red"}
execute align xyz positioned ~3.5 ~ ~0.5 run function shatteredseals:boss/summon_smuggler_king
execute unless score @s ss_spawnok matches 1 run return 0
clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"smuggler_king"}] 1
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Smuggler King Summoning Sigil consumed. The encounter has begun!","color":"gold"}
