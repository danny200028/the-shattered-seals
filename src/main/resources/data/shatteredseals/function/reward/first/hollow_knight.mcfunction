tellraw @s {"text":"Boss defeated: Hollow Knight! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/wardens_oath/dormant
give @s minecraft:bone 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/hollow_knight

function shatteredseals:progression/boss_xp
