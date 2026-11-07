execute store success score @s ss_spawnok run summon minecraft:evoker ~ ~ ~ {Tags:["ss_boss","ss_arcane_seer"],CustomName:'{"text":"Arcane Seer","color":"aqua","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:180.0f,DeathLootTable:"shatteredseals:boss/arcane_seer",attributes:[{id:"minecraft:generic.max_health",base:180.0d},{id:"minecraft:generic.attack_damage",base:11.0d},{id:"minecraft:generic.movement_speed",base:0.28d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_arcane_seer,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 180
execute as @e[tag=ss_arcane_seer,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 11
execute as @e[tag=ss_arcane_seer,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.28
execute as @e[tag=ss_arcane_seer,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:180.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_arcane_seer,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_arcane_seer"]}
execute as @e[tag=ss_arcane_seer,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_arcane_seer,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 1.. run effect give @e[tag=ss_arcane_seer,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
tellraw @s {"text":"Arcane Seer has awakened. Prepare for three phases!","color":"aqua","bold":true}
