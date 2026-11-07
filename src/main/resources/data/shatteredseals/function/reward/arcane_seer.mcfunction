# Fixed: ensure the scoreboard exists for this player and prevent fallback double-awards.
scoreboard players add @s ss_vk_arca 0
execute at @s run kill @e[type=minecraft:marker,tag=ss_watch_arcane_seer,sort=nearest,limit=1,distance=..80]
# Each player gets the original unique boss reward exactly once in this world.
execute if score @s ss_vk_arca matches 0 run function shatteredseals:reward/first/arcane_seer
execute if score @s ss_vk_arca matches 1.. run function shatteredseals:reward/repeat/arcane_seer
scoreboard players add @s ss_vk_arca 1
advancement revoke @s only shatteredseals:victory/arcane_seer
