tellraw @s {"text":"Boss defeated: Smuggler King! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/kings_fang/dormant
give @s minecraft:iron_ingot 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/smuggler_king

function shatteredseals:progression/boss_xp
give @s shatteredseals:boss_mask_smuggler_king 1
