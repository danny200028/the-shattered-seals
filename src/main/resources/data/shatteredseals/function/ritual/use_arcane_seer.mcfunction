# Chapter 4 requires defeating Hollow Knight first.
execute unless score @s ss_vk_holl matches 1.. run tellraw @s {"text":"The next seal remains locked. Defeat Hollow Knight first.","color":"red"}
execute if score @s ss_vk_holl matches 1.. if entity @e[tag=ss_boss,distance=..80] run tellraw @s {"text":"Another Shattered Seals boss is already nearby. Defeat it first.","color":"red"}
execute if score @s ss_vk_holl matches 1.. unless entity @e[tag=ss_boss,distance=..80] run function shatteredseals:ritual/activate_arcane_seer
