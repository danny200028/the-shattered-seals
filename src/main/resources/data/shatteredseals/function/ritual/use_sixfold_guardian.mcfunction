execute unless items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{ss_ritual:"sixfold_guardian"}] run return 0
execute unless score @s ss_vk_arca matches 1.. run return run tellraw @s {"text":"The next seal is locked. Defeat the previous chapter boss first.","color":"red"}
execute if score @s ss_ngchapter matches 1..6 unless score @s ss_ngchapter matches 5 run return run tellraw @s {"text":"Follow the current New Game+ chapter in your Codex.","color":"red"}
execute unless predicate shatteredseals:arena/sixfold_temple run return run tellraw @s {"text":"Take the Sixfold Guardian sigil to the crying obsidian ritual pad in its dungeon.","color":"red"}
execute unless block ~ ~-1 ~ minecraft:crying_obsidian run return run tellraw @s {"text":"Stand on the crying obsidian pad in the boss arena and use the sigil.","color":"yellow"}
execute if entity @e[type=minecraft:marker,tag=ss_runed_arena,tag=ss_chapter_5,distance=..2] unless score @s ss_rune5 matches 3 run return run tellraw @s {"text":"The arena is warded. Read the keeper’s clue and attune the Sun, Tide and Ash runes in order.","color":"light_purple"}
execute if entity @e[tag=ss_boss,distance=..80] run return run tellraw @s {"text":"A Shattered Seals boss is already nearby.","color":"red"}
function shatteredseals:ritual/activate_sixfold_guardian
