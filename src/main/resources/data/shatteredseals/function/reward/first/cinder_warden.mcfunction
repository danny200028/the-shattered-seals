tellraw @s {"text":"Boss defeated: Cinder Warden! First victory: a legendary weapon has been earned.","color":"gold","bold":false}
function shatteredseals:gear/emberbound/dormant
give @s minecraft:blaze_powder 4
tellraw @s {"text":"Additional Warden crafting materials claimed.","color":"aqua","italic":false}
advancement grant @s only shatteredseals:progress/cinder_warden

function shatteredseals:progression/boss_xp
give @s shatteredseals:boss_mask_cinder_warden 1
