tellraw @s {"text":"Boss defeated: Arcane Seer! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/starfall/dormant
give @s minecraft:lapis_lazuli 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/arcane_seer

function shatteredseals:progression/boss_xp
give @s shatteredseals:boss_mask_arcane_seer 1
