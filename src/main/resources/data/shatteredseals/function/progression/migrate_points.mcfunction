# One-time bonus: previous versions granted 1 SP/level, new version grants 3.
scoreboard players operation @s ss_migration = @s ss_level
scoreboard players remove @s ss_migration 1
scoreboard players operation @s ss_migration *= #two ss_migration
scoreboard players operation @s ss_points += @s ss_migration
scoreboard players set @s ss_points_v130 1
tellraw @s [{"text":"Progression update: awarded ","color":"green"},{"score":{"name":"@s","objective":"ss_migration"}},{"text":" bonus skill points for your existing levels.","color":"green"}]
