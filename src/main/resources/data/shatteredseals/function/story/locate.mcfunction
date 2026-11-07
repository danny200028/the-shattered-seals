# Locate the currently active natural jigsaw dungeon for this player.
execute if score @s ss_ngchapter matches 1 run return run locate structure shatteredseals:smugglers_den
execute if score @s ss_ngchapter matches 2 run return run locate structure shatteredseals:cinder_citadel
execute if score @s ss_ngchapter matches 3 run return run locate structure shatteredseals:hollow_crypt
execute if score @s ss_ngchapter matches 4 run return run locate structure shatteredseals:arcane_observatory
execute if score @s ss_ngchapter matches 5 run return run locate structure shatteredseals:sixfold_temple
execute if score @s ss_ngchapter matches 6 run return run locate structure shatteredseals:hollow_kings_fortress
tellraw @s {"text":"Searching for your active chapter dungeon. It may be some distance away.","color":"aqua"}
execute unless score @s ss_vk_smug matches 1.. run locate structure shatteredseals:smugglers_den
execute if score @s ss_vk_smug matches 1.. unless score @s ss_vk_cind matches 1.. run locate structure shatteredseals:cinder_citadel
execute if score @s ss_vk_cind matches 1.. unless score @s ss_vk_holl matches 1.. run locate structure shatteredseals:hollow_crypt
execute if score @s ss_vk_holl matches 1.. unless score @s ss_vk_arca matches 1.. run locate structure shatteredseals:arcane_observatory
execute if score @s ss_vk_arca matches 1.. unless score @s ss_vk_six matches 1.. run locate structure shatteredseals:sixfold_temple
execute if score @s ss_vk_six matches 1.. unless score @s ss_vk_king matches 1.. run locate structure shatteredseals:hollow_kings_fortress
execute if score @s ss_vk_king matches 1.. run tellraw @s {"text":"All six dungeons completed. Begin New Game+ when ready.","color":"green"}
