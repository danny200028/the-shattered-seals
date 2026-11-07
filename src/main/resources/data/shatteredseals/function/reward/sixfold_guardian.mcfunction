# Fixed: ensure the scoreboard exists for this player and prevent fallback double-awards.
scoreboard players add @s ss_vk_six 0
execute at @s run kill @e[type=minecraft:marker,tag=ss_watch_sixfold_guardian,sort=nearest,limit=1,distance=..80]
# Each player gets the original unique boss reward exactly once in this world.
execute if score @s ss_vk_six matches 0 run function shatteredseals:reward/first/sixfold_guardian
execute if score @s ss_vk_six matches 1.. run function shatteredseals:reward/repeat/sixfold_guardian
scoreboard players add @s ss_vk_six 1
advancement revoke @s only shatteredseals:victory/sixfold_guardian
