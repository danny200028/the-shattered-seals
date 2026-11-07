execute unless score @s ss_level matches 60.. run tellraw @s {"text":"New Game+ mastery requires character level 60.","color":"red"}
execute if score @s ss_level matches 60.. if score @s ss_won matches 1.. run scoreboard players add @s ss_ng 1
execute if score @s ss_level matches 60.. if score @s ss_won matches 1.. run scoreboard players set @s ss_won 0
execute if score @s ss_ng matches 1.. run tellraw @s {"text":"New Game+ activated. The next six bosses gain additional power.","color":"dark_purple","bold":true}
execute unless score @s ss_ng matches 1.. run tellraw @s {"text":"Defeat the Hollow King first to unlock New Game+.","color":"red","bold":false}
