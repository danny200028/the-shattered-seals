tellraw @s {"text":"Boss defeated: Sixfold Guardian! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/gravekeeper/dormant
give @s minecraft:diamond 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/sixfold_guardian

function shatteredseals:progression/boss_xp
