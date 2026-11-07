execute store success score @s ss_spawnok run summon minecraft:ravager ~ ~ ~ {Tags:["ss_boss","ss_sixfold_guardian"],CustomName:'{"text":"Sixfold Guardian","color":"yellow","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:310.0f,DeathLootTable:"shatteredseals:boss/sixfold_guardian",attributes:[{id:"minecraft:generic.max_health",base:310.0d},{id:"minecraft:generic.attack_damage",base:16.0d},{id:"minecraft:generic.movement_speed",base:0.3d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_sixfold_guardian,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 310
execute as @e[tag=ss_sixfold_guardian,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 16
execute as @e[tag=ss_sixfold_guardian,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.3
execute as @e[tag=ss_sixfold_guardian,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:310.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_sixfold_guardian,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_sixfold_guardian"]}
execute as @e[tag=ss_sixfold_guardian,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
tellraw @s {"text":"Sixfold Guardian has awakened. Prepare for three phases!","color":"yellow","bold":true}
