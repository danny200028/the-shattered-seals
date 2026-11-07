# Fixed: ensure the scoreboard exists for this player and prevent fallback double-awards.
scoreboard players add @s ss_vk_cind 0
execute at @s run kill @e[type=minecraft:marker,tag=ss_watch_cinder_warden,sort=nearest,limit=1,distance=..80]
# Each player gets the original unique boss reward exactly once in this world.
execute if score @s ss_vk_cind matches 0 run function shatteredseals:reward/first/cinder_warden
execute if score @s ss_vk_cind matches 1.. run function shatteredseals:reward/repeat/cinder_warden
scoreboard players add @s ss_vk_cind 1
execute if score @s ss_ngchapter matches 2 run tellraw @s {"text":"New Game+ chapter cleared. Open the Codex to find the next dungeon.","color":"light_purple"}
execute if score @s ss_ngchapter matches 2 run scoreboard players add @s ss_ngchapter 1
advancement revoke @s only shatteredseals:victory/cinder_warden
