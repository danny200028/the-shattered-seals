# The six-story campaign is driven by real server-side boss victory counters.
tellraw @s {"text":"THE SHATTERED SEALS | Six Chapter Quest Journal","color":"gold","bold":true}
tellraw @s {"text":"Defeat each dungeon guardian, claim a Summoning Sigil from its loot chest, and break the seals in order.","color":"gray"}
execute unless score @s ss_vk_smug matches 1.. run tellraw @s {"text":"Chapter 1: The Smugglers' Den (plains). Follow the smugglers' trail across the plains and challenge their king.","color":"yellow"}
execute if score @s ss_vk_smug matches 1.. unless score @s ss_vk_cind matches 1.. run tellraw @s {"text":"Chapter 2: The Cinder Citadel (desert). The ash sigil leads to a burning citadel hidden among the dunes.","color":"yellow"}
execute if score @s ss_vk_cind matches 1.. unless score @s ss_vk_holl matches 1.. run tellraw @s {"text":"Chapter 3: The Hollow Crypt (forest). The undead guard an ancient crypt deep in the woodland.","color":"yellow"}
execute if score @s ss_vk_holl matches 1.. unless score @s ss_vk_arca matches 1.. run tellraw @s {"text":"Chapter 4: The Arcane Observatory (snowy plains). An observatory beyond the frost watches the shattered sky.","color":"yellow"}
execute if score @s ss_vk_arca matches 1.. unless score @s ss_vk_six matches 1.. run tellraw @s {"text":"Chapter 5: The Sixfold Temple (jungle). The jungle conceals an ancient temple with six fractured wards.","color":"yellow"}
execute if score @s ss_vk_six matches 1.. unless score @s ss_vk_king matches 1.. run tellraw @s {"text":"Chapter 6: The Hollow King's Fortress (dark forest). The last seal lies behind the gates of the Hollow King's fortress.","color":"yellow"}
execute if score @s ss_vk_king matches 1.. run tellraw @s {"text":"The six seals are broken. Reach level 60 and begin New Game+!","color":"light_purple","bold":true}
tellraw @s [{"text":"[LOCATE NEXT DUNGEON]","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_story set 2"}},{"text":"  [START NEW GAME+]","color":"light_purple","clickEvent":{"action":"run_command","value":"/trigger ss_story set 3"}}]
