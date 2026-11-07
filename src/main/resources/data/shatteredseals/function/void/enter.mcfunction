execute if score @s ss_won matches 1.. in shatteredseals:shattered_void positioned 0 90 0 run fill -6 89 -6 6 89 6 minecraft:obsidian
execute if score @s ss_won matches 1.. in shatteredseals:shattered_void run tp @s 0 90 0
execute unless score @s ss_won matches 1.. run tellraw @s {"text":"The Shattered Void opens only after defeating the Hollow King.","color":"red","bold":false}
