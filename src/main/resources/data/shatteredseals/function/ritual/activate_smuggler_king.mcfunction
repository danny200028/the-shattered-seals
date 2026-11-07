clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"smuggler_king"}] 1
function shatteredseals:boss/summon_smuggler_king
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Smuggler King Summoning Sigil consumed. The encounter has begun!","color":"gold","italic":false}
