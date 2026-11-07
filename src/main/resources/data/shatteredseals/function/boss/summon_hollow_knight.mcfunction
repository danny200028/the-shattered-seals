execute store success score @s ss_spawnok run summon minecraft:wither_skeleton ~ ~ ~ {Tags:["ss_boss","ss_hollow_knight"],CustomName:'{"text":"Hollow Knight","color":"dark_purple","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:240.0f,DeathLootTable:"shatteredseals:boss/hollow_knight",attributes:[{id:"minecraft:generic.max_health",base:240.0d},{id:"minecraft:generic.attack_damage",base:14.0d},{id:"minecraft:generic.movement_speed",base:0.32d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_hollow_knight,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 240
execute as @e[tag=ss_hollow_knight,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 14
execute as @e[tag=ss_hollow_knight,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.32
execute as @e[tag=ss_hollow_knight,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:240.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_hollow_knight,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_hollow_knight"]}
execute as @e[tag=ss_hollow_knight,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_hollow_knight,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_hollow_knight,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
tellraw @s {"text":"Hollow Knight has awakened. Prepare for three phases!","color":"dark_purple","bold":true}
