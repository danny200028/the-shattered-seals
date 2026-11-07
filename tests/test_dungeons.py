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
                self.assertEqual(data[:3],b"\\x0a\\x00\\x00")
                self.assertIn(b"minecraft:chest",data)
                self.assertIn(b"minecraft:spawner",data)
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
        self.assertIn("NEW GAME+",journal)
