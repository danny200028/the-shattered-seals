scoreboard players set @s ss_bt 0
particle minecraft:soul_fire_flame ~ ~1 ~ 2 1 2 0.08 90 force
effect give @a[distance=..6,gamemode=!creative,gamemode=!spectator] minecraft:darkness 5 0 true
playsound minecraft:entity.warden.roar hostile @a[distance=..48] ~ ~ ~ 0.6 1
