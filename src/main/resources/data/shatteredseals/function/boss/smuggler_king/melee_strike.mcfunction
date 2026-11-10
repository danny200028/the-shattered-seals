# One close-range sword strike, once per 30 ticks while a valid target is near.
# Use a visible sword for the wind-down; restore the crossbow after eight ticks.
scoreboard players set @s ss_melee 0
item replace entity @s weapon.mainhand with minecraft:iron_sword
execute facing entity @p[distance=..2.6,gamemode=!creative,gamemode=!spectator] eyes run tp @s ~ ~ ~ ~ ~
damage @p[distance=..2.6,gamemode=!creative,gamemode=!spectator] 9 minecraft:mob_attack by @s
particle minecraft:sweep_attack ~ ~1 ~ 0.6 0.2 0.6 0.01 8 force
playsound minecraft:entity.player.attack.sweep hostile @a[distance=..24] ~ ~ ~ 0.9 0.85
