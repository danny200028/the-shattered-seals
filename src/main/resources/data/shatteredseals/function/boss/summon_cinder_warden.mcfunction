execute store success score @s ss_spawnok run summon minecraft:husk ~ ~ ~ {Tags:["ss_boss","ss_cinder_warden"],CustomName:'{"text":"Cinder Warden","color":"red","bold":true}',CustomNameVisible:1b,PersistenceRequired:1b,Health:170.0f,DeathLootTable:"shatteredseals:boss/cinder_warden",attributes:[{id:"minecraft:generic.max_health",base:170.0d},{id:"minecraft:generic.attack_damage",base:12.0d},{id:"minecraft:generic.movement_speed",base:0.3d}]}
execute unless score @s ss_spawnok matches 1 run return 0
execute as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.max_health base set 170
execute as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.attack_damage base set 12
execute as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run attribute @s minecraft:generic.movement_speed base set 0.3
execute as @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..3] run data merge entity @s {Health:170.0f}
# Reward fallback watcher: invisible marker attached to the new boss.
execute as @e[tag=ss_cinder_warden,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] at @s run summon minecraft:marker ~ ~ ~ {Tags:["ss_boss_watch","ss_watch_cinder_warden"]}
execute as @e[tag=ss_cinder_warden,tag=!ss_watch_linked,sort=nearest,limit=1,distance=..4] run tag @s add ss_watch_linked
function shatteredseals:boss/start_heartbeat
# Every NG+ circuit raises the challenge, with a cap at cycle four.
execute if score @s ss_ng matches 1 run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 2 run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 3 true
execute if score @s ss_ng matches 3 run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 4 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 5 true
execute if score @s ss_ng matches 1..3 run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_cinder_warden,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 1 true
tellraw @s {"text":"Cinder Warden has awakened. Prepare for three phases!","color":"red","bold":true}

execute if score @s ss_spawnok matches 1 run item replace entity @e[tag=ss_cinder_warden,sort=nearest,limit=1,distance=..10] armor.head with shatteredseals:boss_mask_cinder_warden
# Cinematic encounter cue: title remains readable and does not freeze the fight.
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] times 8 48 16
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] title {"text":"CINDER WARDEN","color":"red","bold":true}
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] subtitle {"text":"The citadel remembers its fire","color":"gray","italic":true}
execute if score @s ss_spawnok matches 1 run playsound shatteredseals:music.boss master @a[distance=..40] ~ ~ ~ 0.8 1.0
execute if score @s ss_spawnok matches 1 run particle minecraft:flame ~ ~1 ~ 1.8 1.5 1.8 0.04 60 force @a[distance=..40]
