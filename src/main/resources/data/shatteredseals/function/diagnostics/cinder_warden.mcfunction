scoreboard players add @s ss_vk_cind 0
tellraw @s [{"text":"Cinder Warden recorded victories: ","color":"gold"},{"score":{"name":"@s","objective":"ss_vk_cind"}}]
execute if entity @e[type=minecraft:marker,tag=ss_watch_cinder_warden,distance=..80] run tellraw @s {"text":"Reward watcher found nearby.","color":"green"}
execute unless entity @e[type=minecraft:marker,tag=ss_watch_cinder_warden,distance=..80] run tellraw @s {"text":"No reward watcher nearby. Summon a fresh boss to test.","color":"gray"}
