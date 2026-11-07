# Each minion belongs to its summoning boss and despawns on that boss's defeat.
# Nearby checks avoid removing other encounters while their boss is alive.
execute as @e[tag=ss_minion_smuggler_king] at @s unless entity @e[tag=ss_smuggler_king,distance=..96] run kill @s
execute as @e[tag=ss_minion_cinder_warden] at @s unless entity @e[tag=ss_cinder_warden,distance=..96] run kill @s
execute as @e[tag=ss_minion_hollow_knight] at @s unless entity @e[tag=ss_hollow_knight,distance=..96] run kill @s
execute as @e[tag=ss_minion_arcane_seer] at @s unless entity @e[tag=ss_arcane_seer,distance=..96] run kill @s
execute as @e[tag=ss_minion_sixfold_guardian] at @s unless entity @e[tag=ss_sixfold_guardian,distance=..96] run kill @s
execute as @e[tag=ss_minion_hollow_king] at @s unless entity @e[tag=ss_hollow_king,distance=..96] run kill @s
# Clear orphaned, unowned minions left in upgraded pre-v1.0.47 worlds.
execute as @e[tag=ss_minion,tag=!ss_minion_smuggler_king,tag=!ss_minion_cinder_warden,tag=!ss_minion_hollow_knight,tag=!ss_minion_arcane_seer,tag=!ss_minion_sixfold_guardian,tag=!ss_minion_hollow_king] at @s unless entity @e[tag=ss_boss,distance=..96] run kill @s
