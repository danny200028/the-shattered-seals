clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"arcane_seer"}] 1
function shatteredseals:boss/summon_arcane_seer
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Arcane Seer Summoning Sigil consumed. The encounter has begun!","color":"aqua","italic":false}
