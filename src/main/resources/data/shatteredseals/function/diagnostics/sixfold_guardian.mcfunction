scoreboard players add @s ss_vk_six 0
tellraw @s [{"text":"Sixfold Guardian recorded victories: ","color":"gold"},{"score":{"name":"@s","objective":"ss_vk_six"}}]
execute if entity @e[type=minecraft:marker,tag=ss_watch_sixfold_guardian,distance=..80] run tellraw @s {"text":"Reward watcher found nearby.","color":"green"}
execute unless entity @e[type=minecraft:marker,tag=ss_watch_sixfold_guardian,distance=..80] run tellraw @s {"text":"No reward watcher nearby. Summon a fresh boss to test.","color":"gray"}
