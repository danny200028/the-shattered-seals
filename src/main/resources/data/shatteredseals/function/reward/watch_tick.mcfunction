# 1.0.5: robust boss reward fallback. Once per loaded world tick.
execute as @e[tag=ss_smuggler_king] at @s run tp @e[type=minecraft:marker,tag=ss_watch_smuggler_king,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_smuggler_king] at @s unless entity @e[tag=ss_smuggler_king,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/smuggler_king
execute as @e[tag=ss_cinder_warden] at @s run tp @e[type=minecraft:marker,tag=ss_watch_cinder_warden,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_cinder_warden] at @s unless entity @e[tag=ss_cinder_warden,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/cinder_warden
execute as @e[tag=ss_hollow_knight] at @s run tp @e[type=minecraft:marker,tag=ss_watch_hollow_knight,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_hollow_knight] at @s unless entity @e[tag=ss_hollow_knight,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/hollow_knight
execute as @e[tag=ss_arcane_seer] at @s run tp @e[type=minecraft:marker,tag=ss_watch_arcane_seer,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_arcane_seer] at @s unless entity @e[tag=ss_arcane_seer,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/arcane_seer
execute as @e[tag=ss_sixfold_guardian] at @s run tp @e[type=minecraft:marker,tag=ss_watch_sixfold_guardian,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_sixfold_guardian] at @s unless entity @e[tag=ss_sixfold_guardian,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/sixfold_guardian
execute as @e[tag=ss_hollow_king] at @s run tp @e[type=minecraft:marker,tag=ss_watch_hollow_king,sort=nearest,limit=1,distance=..96] ~ ~ ~
execute as @e[type=minecraft:marker,tag=ss_watch_hollow_king] at @s unless entity @e[tag=ss_hollow_king,distance=..48] if entity @p[distance=..80,gamemode=!spectator] run function shatteredseals:reward/fallback/hollow_king
