scoreboard players add @s ss_vk_holl 0
tellraw @s [{"text":"Hollow Knight recorded victories: ","color":"gold"},{"score":{"name":"@s","objective":"ss_vk_holl"}}]
execute if entity @e[type=minecraft:marker,tag=ss_watch_hollow_knight,distance=..80] run tellraw @s {"text":"Reward watcher found nearby.","color":"green"}
execute unless entity @e[type=minecraft:marker,tag=ss_watch_hollow_knight,distance=..80] run tellraw @s {"text":"No reward watcher nearby. Summon a fresh boss to test.","color":"gray"}
