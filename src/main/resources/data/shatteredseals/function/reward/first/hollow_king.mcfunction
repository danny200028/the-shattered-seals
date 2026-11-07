tellraw @s {"text":"Boss defeated: Hollow King! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/kings_bane/dormant
scoreboard players set @s ss_won 1
tellraw @s {"text":"The Hollow King is defeated! New Game+ is now unlocked.","color":"green","bold":true}
give @s minecraft:netherite_scrap 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/hollow_king

function shatteredseals:progression/boss_xp
