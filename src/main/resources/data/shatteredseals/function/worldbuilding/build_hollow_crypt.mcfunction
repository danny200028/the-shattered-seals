tellraw @s {"text":"Building a prototype landmark near your position. This is NOT naturally generated.","color":"yellow"}
execute positioned ~8 ~ ~8 run fill ~-5 ~ ~-5 ~5 ~5 ~5 air
execute positioned ~8 ~ ~8 run fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 minecraft:tuff_bricks
execute positioned ~8 ~ ~8 run fill ~-5 ~ ~-5 ~5 ~4 ~5 minecraft:tuff_bricks hollow
execute positioned ~8 ~ ~8 run fill ~-4 ~ ~-4 ~4 ~3 ~4 air
execute positioned ~8 ~ ~8 run fill ~-1 ~ ~-5 ~1 ~2 ~-5 air
execute positioned ~8 ~ ~8 run setblock ~0 ~0 ~0 minecraft:chest{LootTable:"shatteredseals:chests/hollow_crypt"}
execute positioned ~8 ~ ~8 run summon minecraft:villager ~2 ~1 ~2 {CustomName:'{"text":"Hollow Crypt Quartermaster"}',PersistenceRequired:1b,Invulnerable:1b,VillagerData:{profession:"minecraft:weaponsmith",level:3,type:"minecraft:plains"}}
execute positioned ~8 ~ ~8 run fill ~-3 ~-13 ~-3 ~3 ~-8 ~3 air
execute positioned ~8 ~ ~8 run fill ~-4 ~-14 ~-4 ~4 ~-14 ~4 minecraft:tuff_bricks
execute positioned ~8 ~ ~8 run fill ~-4 ~-13 ~-4 ~4 ~-8 ~4 minecraft:tuff_bricks hollow
execute positioned ~8 ~ ~8 run fill ~-3 ~-13 ~-3 ~3 ~-9 ~3 air
execute positioned ~8 ~ ~8 run setblock ~0 ~-13 ~0 minecraft:chest{LootTable:"shatteredseals:chests/hollow_crypt"}
execute positioned ~8 ~ ~8 run summon minecraft:wither_skeleton ~2 ~-12 ~2 {Tags:["ss_elite_hollow"],PersistenceRequired:1b,CustomName:'{"text": "Grave Sentinel", "color": "dark_red", "italic": false}'}
tellraw @s {"text": "Hollow Crypt prototype outpost created. Underground vault is sealed; use Spectator mode to inspect safely.", "color": "gold"}
