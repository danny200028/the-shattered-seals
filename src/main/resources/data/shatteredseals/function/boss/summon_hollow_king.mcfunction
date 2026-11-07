execute store success score @s ss_spawnok run summon minecraft:ravager ~ ~ ~ {Tags:["ss_boss","ss_hollow_king"],CustomName:'{"text":"Hollow King","color":"dark_red","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:450.0f,DeathLootTable:"shatteredseals:boss/hollow_king",attributes:[{id:"minecraft:generic.max_health",base:450.0d},{id:"minecraft:generic.attack_damage",base:20.0d},{id:"minecraft:generic.movement_speed",base:0.31d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_hollow_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 450
execute as @e[tag=ss_hollow_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 20
execute as @e[tag=ss_hollow_king,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.31
execute as @e[tag=ss_hollow_king,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:450.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_hollow_king,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_hollow_king"]}
execute as @e[tag=ss_hollow_king,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
# Every NG+ circuit raises the challenge, with a cap at cycle four.
execute if score @s ss_ng matches 1 run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 2 run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 3 true
execute if score @s ss_ng matches 3 run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 4 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 5 true
execute if score @s ss_ng matches 1..3 run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_hollow_king,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 1 true
tellraw @s {"text":"Hollow King has awakened. Prepare for three phases!","color":"dark_red","bold":true}
