clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"sixfold_guardian"}] 1
function shatteredseals:boss/summon_sixfold_guardian
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Sixfold Guardian Summoning Sigil consumed. The encounter has begun!","color":"yellow","italic":false}
