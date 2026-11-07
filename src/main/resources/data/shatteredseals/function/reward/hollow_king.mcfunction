# Fixed: ensure the scoreboard exists for this player and prevent fallback double-awards.
scoreboard players add @s ss_vk_king 0
execute at @s run kill @e[type=minecraft:marker,tag=ss_watch_hollow_king,sort=nearest,limit=1,distance=..80]
# Each player gets the original unique boss reward exactly once in this world.
execute if score @s ss_vk_king matches 0 run function shatteredseals:reward/first/hollow_king
execute if score @s ss_vk_king matches 1.. run function shatteredseals:reward/repeat/hollow_king
scoreboard players add @s ss_vk_king 1
advancement revoke @s only shatteredseals:victory/hollow_king
