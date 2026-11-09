# Runs only inside the successful original random-chance proc; never rolls twice.
execute if entity @s[type=minecraft:player,tag=!ss_quiet_enchants,scores={ss_proc_cd=0}] run tellraw @s {"text":"✦ Soul Reaper activated!","color":"gold","italic":false}
execute if entity @s[type=minecraft:player,scores={ss_proc_cd=0}] run scoreboard players set @s ss_proc_cd 40
