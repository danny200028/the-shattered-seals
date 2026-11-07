clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"hollow_knight"}] 1
function shatteredseals:boss/summon_hollow_knight
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Hollow Knight Summoning Sigil consumed. The encounter has begun!","color":"dark_purple","italic":false}
