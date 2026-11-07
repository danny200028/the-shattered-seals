execute store success score @s ss_spawnok run summon minecraft:pillager ~ ~ ~ {Tags:["ss_boss","ss_smuggler_king"],CustomName:'{"text":"Smuggler King","color":"gold","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:150.0f,DeathLootTable:"shatteredseals:boss/smuggler_king",attributes:[{id:"minecraft:generic.max_health",base:150.0d},{id:"minecraft:generic.attack_damage",base:9.0d},{id:"minecraft:generic.movement_speed",base:0.33d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_smuggler_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 150
execute as @e[tag=ss_smuggler_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 9
execute as @e[tag=ss_smuggler_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.33
execute as @e[tag=ss_smuggler_king,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:150.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_smuggler_king,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_smuggler_king"]}
execute as @e[tag=ss_smuggler_king,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_smuggler_king,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_smuggler_king,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
tellraw @s {"text":"Smuggler King has awakened. Prepare for three phases!","color":"gold","bold":true}
