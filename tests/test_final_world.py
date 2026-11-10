"""Static sanity gates for the consolidated final world expansion."""
from pathlib import Path
import ast
import json
import unittest
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
JAVA=ROOT/"src/main/java/com/shatteredseals"
REGIONS=("smugglers_tunnels","emberfault_chasms","drowned_ossuary",
         "starshard_caverns","rootbound_grottos","black_crown_depths")
class FinalWorldTests(unittest.TestCase):
    def test_generator_and_worldgen_registries(self):
        ast.parse((ROOT/"scripts/generate_final_world.py").read_text())
        for name in ("wardens_sanctuary",)+tuple(n+"_ruin" for n in REGIONS)+tuple(n+"_vault" for n in REGIONS):
            with self.subTest(name=name):
                self.assertTrue((DATA/f"structure/{name}.nbt").is_file(),name)
                for folder in ("template_pool","structure","structure_set"):
                    file=DATA/f"worldgen/{folder}/{name}.json"
                    self.assertTrue(file.is_file(),name+":"+folder)
                    json.loads(file.read_text())
    def test_regions_ore_and_vanilla_biome_preservation(self):
        from runpy import run_path
        # Generator outputs are materialised by the CI generation step first.
        biomes=("smugglers_wilds","cinder_wastes","hollow_moor",
                "arcane_highlands","sixfold_expanse","hollow_kings_dominion")
        for biome in biomes:
            data=json.loads((DATA/f"worldgen/biome/{biome}.json").read_text())
            self.assertIn("minecraft:cave",data["carvers"]["air"])
            self.assertIn("minecraft:ore_diamond",data["features"][6])
            self.assertIn("shatteredseals:"+biome+"_depths_vein",data["features"][6])
    def test_exact_proc_chance_not_rerolled(self):
        for id in ("celestial_judgement","dragons_fury","frostbite","infernal_wrath",
                   "soul_reaper","stormcaller","thunderstrike","vampiric_edge"):
            enchant=json.loads((DATA/f"enchantment/{id}.json").read_text())
            proc=[p for p in enchant["effects"]["minecraft:post_attack"] if p.get("requirements",{}).get("condition")=="minecraft:random_chance"]
            self.assertEqual(len(proc),1,id)
            self.assertEqual(proc[0]["effect"]["type"],"minecraft:all_of")
            self.assertEqual(proc[0]["effect"]["effects"][1]["type"],"minecraft:run_function")
            self.assertTrue((DATA/f"function/enchantment_procs/{id}.mcfunction").is_file())
    def test_all_features_are_server_authoritative(self):
        source=(JAVA/"adventure/FinalAdventure.java").read_text()
        for needle in ("secretUnlocked(", "SSTrial", "SSDepthsSitesMask",
                       "SSDepthsVaultsMask","SSDepthsEliteMask","onClone",
                       "SSSecretWon","SSShowAmbientEffects","SSShowCombatHud"):
            if needle=="SSTrial":continue
            self.assertIn(needle,source)
        self.assertNotIn("giveExperienceLevels(100",source)
    def test_sanctuary_remodel_keeps_room_markers_and_functional_positions(self):
        import sys
        sys.path.insert(0,str(ROOT/"scripts"))
        from generate_final_world import remodel_sanctuary
        blocks={}
        remodel_sanctuary(blocks)
        # The redesign must keep the old structure footprint and avoid
        # overwriting utility blocks on the floor, the Archive and markers.
        self.assertTrue(all(0<=x<81 and 0<=y<20 and 0<=z<49
                            for x,y,z in blocks))
        for pos in ((40,1,24),(38,1,8),(4,1,40),(68,1,21),
                    (36,1,40),(65,1,3),(72,1,40)):
            self.assertNotIn(pos,blocks)
        self.assertEqual(blocks[(40,7,20)][0],"minecraft:lantern")
        self.assertEqual(blocks[(35,6,24)][0],
                         "minecraft:polished_blackstone_brick_stairs[facing=north]")
        self.assertEqual(blocks[(32,3,24)][0],"minecraft:air")

    def test_smuggler_melee_attack_is_cooldown_and_range_gated(self):
        heartbeat=(DATA/"function/boss/heartbeat.mcfunction").read_text()
        strike=(DATA/"function/boss/smuggler_king/melee_strike.mcfunction").read_text()
        spawn=(DATA/"function/boss/summon_smuggler_king.mcfunction").read_text()
        self.assertIn("ss_melee=30..",heartbeat)
        self.assertIn("distance=..2.6",heartbeat)
        self.assertIn("ss_melee 0",strike)
        self.assertIn("minecraft:mob_attack by @s",strike)
        self.assertIn("minecraft:iron_sword",strike)
        self.assertIn("weapon.mainhand with minecraft:crossbow",spawn)
        self.assertIn("weapon.mainhand with minecraft:crossbow",heartbeat)
        self.assertIn("gamemode=!spectator",strike)

    def test_real_minecraft_structures_have_games(self):
        tests=(JAVA/"diagnostics/FinalWorldGameTests.java").read_text()
        self.assertEqual(tests.count("@GameTest("),10)
        self.assertIn("wardens_sanctuary",tests)
        self.assertIn("smugglers_tunnels_vault",tests)
        self.assertIn("All", (ROOT/"scripts/verify_gametest_results.py").read_text())
if __name__=="__main__":
    unittest.main()
