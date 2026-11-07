execute if entity @e[tag=ss_boss,distance=..80] run tellraw @s {"text":"Another Shattered Seals boss is already nearby. Defeat it first.","color":"red","italic":false}
execute unless entity @e[tag=ss_boss,distance=..80] run function shatteredseals:ritual/activate_hollow_knight
