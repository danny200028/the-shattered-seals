execute positioned ^ ^ ^6 positioned over motion_blocking run summon minecraft:husk ~ ~ ~ {Tags:["ss_boss","ss_cinder_warden"],CustomName:'{"text":"Cinder Warden","color":"red","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:170.0f,DeathLootTable:"shatteredseals:boss/cinder_warden",attributes:[{id:"minecraft:generic.max_health",base:170.0d},{id:"minecraft:generic.attack_damage",base:12.0d},{id:"minecraft:generic.movement_speed",base:0.3d}]}
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 170
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 12
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.3
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:170.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_cinder_warden"]}
execute positioned ^ ^ ^6 positioned over motion_blocking as @e[tag=ss_cinder_warden,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
execute if score @s ss_ng matches 1.. at @s run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 1.. at @s run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
tellraw @s {"text":"Cinder Warden has awakened. Prepare for three phases!","color":"red","bold":true}
