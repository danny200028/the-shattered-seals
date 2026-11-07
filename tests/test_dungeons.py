"""Regression checks for generated jigsaw dungeons and story ritual gates."""
import gzip
import json
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
DUNGEONS=[
    ("smugglers_den","smuggler_king",None),
    ("cinder_citadel","cinder_warden","ss_vk_smug"),
    ("hollow_crypt","hollow_knight","ss_vk_cind"),
    ("arcane_observatory","arcane_seer","ss_vk_holl"),
    ("sixfold_temple","sixfold_guardian","ss_vk_arca"),
    ("hollow_kings_fortress","hollow_king","ss_vk_six"),
]
class DungeonTests(unittest.TestCase):
    def test_generation_and_gameplay_connection(self):
        for dungeon,boss,previous in DUNGEONS:
            with self.subTest(dungeon=dungeon):
                template=DATA/f"structure/{dungeon}.nbt"
                self.assertTrue(template.exists(),f"missing {template}")
                data=gzip.decompress(template.read_bytes())
                self.assertEqual(data[:3],bytes.fromhex("0a0000"))
                self.assertIn(b"minecraft:chest",data)
                self.assertIn(b"minecraft:spawner",data)
                self.assertGreater(len(data),500000,"Dungeon is not the full nine-room template")
                self.assertIn(b"minecraft:magma_block",data)
                self.assertIn(b"minecraft:cobweb",data)
                self.assertIn(b"minecraft:crying_obsidian",data)
                self.assertIn(b"shatteredseals:chests/dungeon_cache",data)
                self.assertIn(b"minecraft:polished_blackstone",data)
                self.assertIn(b"minecraft:soul_lantern",data)
                # Six guardian spawner blocks with full SpawnData compounds.
                self.assertEqual(data.count(b"SpawnData"),6)
                # One chapter sigil chest and two optional treasure caches.
                self.assertEqual(data.count(b"LootTable"),3)

                self.assertIn(f"shatteredseals:chests/{dungeon}".encode(),data)
                pool=json.loads((DATA/f"worldgen/template_pool/{dungeon}.json").read_text())
                entry=pool["elements"][0]["element"]
                self.assertEqual(entry["location"],f"shatteredseals:{dungeon}")
                self.assertEqual(entry["projection"],"rigid")
                structure=json.loads((DATA/f"worldgen/structure/{dungeon}.json").read_text())
                self.assertEqual(structure["type"],"minecraft:jigsaw")
                self.assertEqual(structure["start_pool"],f"shatteredseals:{dungeon}")
                structure_set=json.loads((DATA/f"worldgen/structure_set/{dungeon}.json").read_text())
                self.assertEqual(structure_set["structures"][0]["structure"],f"shatteredseals:{dungeon}")
                self.assertLess(structure_set["placement"]["separation"],structure_set["placement"]["spacing"])
                loot=json.loads((DATA/f"loot_table/chests/{dungeon}.json").read_text())
                self.assertIn('"ss_ritual": "'+boss+'"',json.dumps(loot))
                if previous:
                    ritual=(DATA/f"function/ritual/use_{boss}.mcfunction").read_text()
                    self.assertIn(f"if score @s {previous} matches 1..",ritual)

    def test_optional_cache_rarity_distribution(self):
        # Bonus treasure should be common-biased even though late-game
        # chapter-specific chests deliberately have better drop rates.
        cache=json.loads((DATA/"loot_table/chests/dungeon_cache.json").read_text())
        entries=cache["pools"][0]["entries"]
        tiers=("common","rare","epic","legendary")
        self.assertEqual(
            [e["value"] for e in entries],
            [f"shatteredseals:repeat/{tier}" for tier in tiers])
        self.assertEqual([e["weight"] for e in entries],[70,20,8,2])
        self.assertEqual(sum(e["weight"] for e in entries),100)

    def test_journal_access_in_survival(self):
        load=(DATA/"function/load.mcfunction").read_text()
        tick=(DATA/"function/progression/tick.mcfunction").read_text()
        journal=(DATA/"function/story/journal.mcfunction").read_text()
        locate=(DATA/"function/story/locate.mcfunction").read_text()
        self.assertIn("scoreboard objectives add ss_story trigger",load)
        self.assertIn("scoreboard players enable @a ss_story",tick)
        self.assertIn("shatteredseals:story/journal",tick)
        self.assertIn("shatteredseals:story/locate",tick)
        self.assertEqual(locate.count("locate structure shatteredseals:"),6)
        self.assertIn("NEW GAME+",journal.upper())
