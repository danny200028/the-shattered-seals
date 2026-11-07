# Remove watchers BEFORE deleting bosses so cleanup does not grant false victories.
kill @e[type=minecraft:marker,tag=ss_boss_watch]
kill @e[tag=ss_boss]
kill @e[tag=ss_minion]
tellraw @s {"text":"All Shattered Seals boss entities and minions removed.","color":"gray","bold":false}
