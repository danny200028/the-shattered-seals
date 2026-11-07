clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"cinder_warden"}] 1
function shatteredseals:boss/summon_cinder_warden
playsound minecraft:block.end_portal.spawn hostile @a[distance=..40] ~ ~ ~ 0.7 1
tellraw @s {"text":"Cinder Warden Summoning Sigil consumed. The encounter has begun!","color":"red","italic":false}
