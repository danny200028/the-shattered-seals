# /trigger ss_mperk set 9 to open the NG+ mastery shop
execute if score @s ss_mperk matches 9 run tellraw @s {"text":"NEW GAME+ MASTERY | Spend earned mastery points (1 per rank, max 5).","color":"light_purple"}
execute if score @s ss_mperk matches 9 run tellraw @s {"text":"[Defender] Buy rank","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_mperk set 1"}}
execute if score @s ss_mperk matches 1 unless score @s ss_ng matches 1.. run tellraw @s {"text":"New Game+ required.","color":"red"}
execute if score @s ss_mperk matches 1 if score @s ss_ng matches 1.. unless score @s ss_mpoints matches 1.. run tellraw @s {"text":"Not enough mastery points.","color":"red"}
execute if score @s ss_mperk matches 1 if score @s ss_mp_guard matches 5.. run tellraw @s {"text":"This mastery perk is maxed.","color":"yellow"}
execute if score @s ss_mperk matches 1 if score @s ss_ng matches 1.. if score @s ss_mpoints matches 1.. if score @s ss_mp_guard matches ..4 run function shatteredseals:mastery/buy_guard
execute if score @s ss_mperk matches 9 run tellraw @s {"text":"[Swiftblade] Buy rank","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_mperk set 2"}}
execute if score @s ss_mperk matches 2 unless score @s ss_ng matches 1.. run tellraw @s {"text":"New Game+ required.","color":"red"}
execute if score @s ss_mperk matches 2 if score @s ss_ng matches 1.. unless score @s ss_mpoints matches 1.. run tellraw @s {"text":"Not enough mastery points.","color":"red"}
execute if score @s ss_mperk matches 2 if score @s ss_mp_haste matches 5.. run tellraw @s {"text":"This mastery perk is maxed.","color":"yellow"}
execute if score @s ss_mperk matches 2 if score @s ss_ng matches 1.. if score @s ss_mpoints matches 1.. if score @s ss_mp_haste matches ..4 run function shatteredseals:mastery/buy_haste
execute if score @s ss_mperk matches 9 run tellraw @s {"text":"[Warbringer] Buy rank","color":"aqua","clickEvent":{"action":"run_command","value":"/trigger ss_mperk set 3"}}
execute if score @s ss_mperk matches 3 unless score @s ss_ng matches 1.. run tellraw @s {"text":"New Game+ required.","color":"red"}
execute if score @s ss_mperk matches 3 if score @s ss_ng matches 1.. unless score @s ss_mpoints matches 1.. run tellraw @s {"text":"Not enough mastery points.","color":"red"}
execute if score @s ss_mperk matches 3 if score @s ss_mp_fury matches 5.. run tellraw @s {"text":"This mastery perk is maxed.","color":"yellow"}
execute if score @s ss_mperk matches 3 if score @s ss_ng matches 1.. if score @s ss_mpoints matches 1.. if score @s ss_mp_fury matches ..4 run function shatteredseals:mastery/buy_fury
