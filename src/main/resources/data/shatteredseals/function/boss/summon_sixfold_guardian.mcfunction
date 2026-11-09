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
# Every NG+ circuit raises the challenge, with a cap at cycle four.
execute if score @s ss_ng matches 1 run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 2 true
execute if score @s ss_ng matches 2 run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 3 true
execute if score @s ss_ng matches 3 run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 4 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:strength 99999 5 true
execute if score @s ss_ng matches 1..3 run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 0 true
execute if score @s ss_ng matches 4.. run effect give @e[tag=ss_sixfold_guardian,distance=..10,sort=nearest,limit=1] minecraft:resistance 99999 1 true
tellraw @s {"text":"Sixfold Guardian has awakened. Prepare for three phases!","color":"yellow","bold":true}

execute if score @s ss_spawnok matches 1 run item replace entity @e[tag=ss_sixfold_guardian,sort=nearest,limit=1,distance=..10] armor.head with shatteredseals:boss_mask_sixfold_guardian
# Cinematic encounter cue: title remains readable and does not freeze the fight.
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] times 8 48 16
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] title {"text":"SIXFOLD GUARDIAN","color":"yellow","bold":true}
execute if score @s ss_spawnok matches 1 run title @a[distance=..40] subtitle {"text":"Six seals. One ancient oath","color":"gray","italic":true}
execute if score @s ss_spawnok matches 1 run playsound shatteredseals:music.boss master @a[distance=..40] ~ ~ ~ 0.8 1.0
execute if score @s ss_spawnok matches 1 run particle minecraft:happy_villager ~ ~1 ~ 1.8 1.5 1.8 0.04 60 force @a[distance=..40]
