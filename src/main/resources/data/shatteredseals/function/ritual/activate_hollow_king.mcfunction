clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"hollow_king"}] 1
function shatteredseals:boss/summon_hollow_king
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Hollow King Summoning Sigil consumed. The encounter has begun!","color":"dark_red","italic":false}
