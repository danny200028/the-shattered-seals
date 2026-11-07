execute unless items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"cinder_warden"}] run return 0
execute unless score @s ss_vk_smug matches 1.. run return run tellraw @s {"text":"The next seal is locked. Defeat the previous chapter boss first.","color":"red"}
execute if score @s ss_ngchapter matches 1..6 unless score @s ss_ngchapter matches 2 run return run tellraw @s {"text":"Follow the current New Game+ chapter in your Codex.","color":"red"}
execute unless predicate shatteredseals:arena/cinder_citadel run return run tellraw @s {"text":"Take the Cinder Warden sigil to the crying obsidian ritual pad in its dungeon.","color":"red"}
execute unless block ~ ~-1 ~ minecraft:crying_obsidian run return run tellraw @s {"text":"Stand on the crying obsidian pad in the boss arena and use the sigil.","color":"yellow"}
execute if entity @e[tag=ss_boss,distance=..80] run return run tellraw @s {"text":"A Shattered Seals boss is already nearby.","color":"red"}
function shatteredseals:ritual/activate_cinder_warden
