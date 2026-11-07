scoreboard players operation @s ss_need = @s ss_level
scoreboard players set #ten ss_need 10
scoreboard players operation @s ss_need *= #ten ss_need
scoreboard players add @s ss_need 30
scoreboard players operation @s ss_mneed = @s ss_master
scoreboard players set #twenty ss_mneed 20
scoreboard players operation @s ss_mneed *= #twenty ss_mneed
scoreboard players add @s ss_mneed 150
