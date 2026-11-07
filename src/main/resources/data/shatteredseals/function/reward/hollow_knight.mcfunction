# Fixed: ensure the scoreboard exists for this player and prevent fallback double-awards.
scoreboard players add @s ss_vk_holl 0
execute at @s run kill @e[type=minecraft:marker,tag=ss_watch_hollow_knight,sort=nearest,limit=1,distance=..80]
# Each player gets the original unique boss reward exactly once in this world.
execute if score @s ss_vk_holl matches 0 run function shatteredseals:reward/first/hollow_knight
execute if score @s ss_vk_holl matches 1.. run function shatteredseals:reward/repeat/hollow_knight
scoreboard players add @s ss_vk_holl 1
advancement revoke @s only shatteredseals:victory/hollow_knight
