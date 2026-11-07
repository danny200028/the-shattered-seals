execute at @s run fill ~-10 ~-1 ~-10 ~10 ~-1 ~10 minecraft:stone_bricks
execute at @s run fill ~-10 ~ ~-10 ~10 ~5 ~10 minecraft:stone_bricks hollow
execute at @s run fill ~-9 ~ ~-9 ~9 ~4 ~9 minecraft:air
execute at @s run fill ~-2 ~ ~-10 ~2 ~3 ~-10 minecraft:air
execute at @s run fill ~-10 ~5 ~-10 ~10 ~5 ~10 minecraft:deepslate_tiles
execute at @s run setblock ~0 ~0 ~0 minecraft:lodestone
execute at @s run setblock ~-7 ~0 ~-7 minecraft:anvil
execute at @s run setblock ~-5 ~0 ~-7 minecraft:smithing_table
execute at @s run setblock ~-3 ~0 ~-7 minecraft:grindstone
execute at @s run setblock ~5 ~0 ~-7 minecraft:enchanting_table
execute at @s run setblock ~7 ~0 ~-7 minecraft:lectern
execute at @s run setblock ~7 ~0 ~7 minecraft:ender_chest
execute at @s run setblock ~-7 ~0 ~7 minecraft:crafting_table
execute at @s run setblock ~-4 ~0 ~7 minecraft:barrel
execute at @s run setblock ~0 ~0 ~-8 minecraft:soul_lantern
execute at @s run setblock ~0 ~4 ~0 minecraft:sea_lantern
tellraw @s {"text":"Warden Sanctuary built at your position. Make sure there was enough empty space first.","color":"aqua","bold":false}
