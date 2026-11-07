tellraw @s {"text":"Cinder Warden diagnosis: the scoreboard value below is tracked health. It should start close to 170 and decline when hit.","color":"gold"}
execute if entity @e[tag=ss_cinder_warden,limit=1] run tellraw @s [{"text":"Tracked Cinder Warden HP: ","color":"aqua"},{"score":{"name":"@e[tag=ss_cinder_warden,limit=1]","objective":"ss_hp"}}]
execute unless entity @e[tag=ss_cinder_warden,limit=1] run tellraw @s {"text":"There is currently no Cinder Warden in loaded chunks.","color":"red"}
