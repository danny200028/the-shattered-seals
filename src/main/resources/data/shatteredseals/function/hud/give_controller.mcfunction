tag @s add ss_hud_controller_given
scoreboard players add @s ss_hudclick 0
scoreboard players operation @s ss_hudlast = @s ss_hudclick
give @s minecraft:warped_fungus_on_a_stick[minecraft:custom_name='{"text":"Shattered Seals HUD Settings","color":"gold","italic":false}',minecraft:lore=['{"text":"Right-click to change your RPG HUD","color":"gray","italic":false}'],minecraft:custom_data={ss_hud_controller:1b}] 1
tellraw @s {"text":"[Shattered Seals] HUD Settings controller added to your inventory. Right-click it to open settings.","color":"aqua"}
