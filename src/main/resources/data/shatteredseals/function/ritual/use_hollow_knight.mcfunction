# Chapter 3 requires defeating Cinder Warden first.
execute unless score @s ss_vk_cind matches 1.. run tellraw @s {"text":"The next seal remains locked. Defeat Cinder Warden first.","color":"red"}
execute if score @s ss_vk_cind matches 1.. if entity @e[tag=ss_boss,distance=..80] run tellraw @s {"text":"Another Shattered Seals boss is already nearby. Defeat it first.","color":"red"}
execute if score @s ss_vk_cind matches 1.. unless entity @e[tag=ss_boss,distance=..80] run function shatteredseals:ritual/activate_hollow_knight
