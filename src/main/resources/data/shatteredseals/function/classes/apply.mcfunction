scoreboard players operation @s ss_path = @s ss_pending
scoreboard players set @s ss_cd 0
tellraw @s {"text":"Class switched! All four classes keep their purchased skills and masteries.","color":"green"}
function shatteredseals:ui/skills_menu
