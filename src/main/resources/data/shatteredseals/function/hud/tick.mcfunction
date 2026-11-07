scoreboard players enable @a ss_hud
execute as @a unless score @s ss_hudinit matches 1 run function shatteredseals:hud/initialize
execute as @a[scores={ss_hud=1}] run function shatteredseals:hud/enable
execute as @a[scores={ss_hud=2}] run function shatteredseals:hud/disable
scoreboard players set @a[scores={ss_hud=1..}] ss_hud 0
scoreboard players add #clock ss_hudclock 1
execute if score #clock ss_hudclock matches 20.. run function shatteredseals:hud/pulse

scoreboard players enable @a ss_hudstyle
execute as @a[scores={ss_hudstyle=1}] run function shatteredseals:hud/style_compact
execute as @a[scores={ss_hudstyle=2}] run function shatteredseals:hud/style_detailed
execute as @a[scores={ss_hudstyle=3..}] run function shatteredseals:hud/style_help
scoreboard players set @a[scores={ss_hudstyle=1..}] ss_hudstyle 0

scoreboard players enable @a ss_hudchoice
execute as @a unless entity @s[tag=ss_hud_controller_given] run function shatteredseals:hud/give_controller
execute as @a if score @s ss_hudclick > @s ss_hudlast run function shatteredseals:hud/open_menu
execute as @a run scoreboard players operation @s ss_hudlast = @s ss_hudclick
execute as @a[scores={ss_hudchoice=1}] run function shatteredseals:hud/style_compact
execute as @a[scores={ss_hudchoice=2}] run function shatteredseals:hud/style_detailed
execute as @a[scores={ss_hudchoice=3}] run function shatteredseals:hud/disable
execute as @a[scores={ss_hudchoice=4}] run function shatteredseals:hud/enable
scoreboard players set @a[scores={ss_hudchoice=1..}] ss_hudchoice 0
