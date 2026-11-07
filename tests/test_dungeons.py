"""Regression checks for generated jigsaw dungeons and story ritual gates."""
import gzip
import importlib.util
import json
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
GENERATOR = importlib.util.spec_from_file_location("dungeons", ROOT / "scripts/generate_dungeons.py")
DUNGEON_SOURCE = importlib.util.module_from_spec(GENERATOR)
GENERATOR.loader.exec_module(DUNGEON_SOURCE)
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
                self.assertIn(b"ss_runed_arena", data)
                self.assertIn(b"ss_keeper", data)
                self.assertIn(b"ss_challenge", data)
                for rune in range(3):
                    self.assertIn(f"ss_rune_{rune}".encode(), data)

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
                    self.assertIn(f"unless score @s {previous} matches 1.. run return",ritual)

    def test_bosses_require_the_matching_arena_and_consume_sigils_after_spawn(self):
        functions = DATA / "function"
        for chapter, (dungeon, boss, _) in enumerate(DUNGEONS, 1):
            with self.subTest(boss=boss):
                predicate = json.loads((DATA / f"predicate/arena/{dungeon}.json").read_text())
                self.assertEqual(predicate["predicate"]["structures"], f"shatteredseals:{dungeon}")
                use = (functions / f"ritual/use_{boss}.mcfunction").read_text()
                self.assertIn(f"unless predicate shatteredseals:arena/{dungeon} run return", use)
                self.assertIn("unless block ~ ~-1 ~ minecraft:crying_obsidian run return", use)
                self.assertIn(f"unless score @s ss_ngchapter matches {chapter} run return", use)
                activate = (functions / f"ritual/activate_{boss}.mcfunction").read_text()
                self.assertLess(activate.index(f"boss/summon_{boss}"), activate.index("clear @s"))
                self.assertIn("unless score @s ss_spawnok matches 1 run return 0", activate)
                summon = (functions / f"boss/summon_{boss}.mcfunction").read_text()
                self.assertNotIn("positioned over", summon)
                self.assertIn("store success score @s ss_spawnok run summon", summon)
                self.assertIn(f"execute unless score @s ss_rune{chapter} matches 3 run return", use)
                self.assertIn(f"unless score @s ss_rune{chapter} matches 3", use)

    def test_dungeon_routes_are_distinct_and_every_room_is_reachable(self):
        layouts = []
        for routes, runes, challenge, _ in DUNGEON_SOURCE.LAYOUTS:
            edges = {tuple(sorted(map(int, edge))) for edge in routes.split()}
            layouts.append(frozenset(edges))
            reached = {1}
            for _ in range(8):
                for first, second in edges:
                    self.assertEqual(abs(first // 3 - second // 3) + abs(first % 3 - second % 3), 1)
                    if first in reached or second in reached:
                        reached.update((first, second))
            self.assertEqual(reached, set(range(9)))
            self.assertEqual(len(set(runes)), 3)
            self.assertIn(challenge, reached)
        self.assertEqual(len(set(layouts)), len(DUNGEONS))

    def test_new_game_plus_replays_without_resetting_first_clear_rewards(self):
        functions = DATA / "function"
        start = (functions / "ngplus/start.mcfunction").read_text()
        self.assertIn("unless score @s ss_level matches 60..", start)
        self.assertIn("unless score @s ss_vk_king matches 1..", start)
        self.assertIn("ss_ngchapter matches 1..6 run return", start)
        self.assertIn("scoreboard players set @s ss_ngchapter 1", start)
        for chapter, (_, boss, _) in enumerate(DUNGEONS, 1):
            with self.subTest(boss=boss):
                reward = (functions / f"reward/{boss}.mcfunction").read_text()
                self.assertIn(f"ss_ngchapter matches {chapter} run scoreboard players add @s ss_ngchapter 1", reward)
                repeat = (functions / f"reward/repeat/{boss}.mcfunction").read_text()
                self.assertIn("ss_ng matches 1.. run function shatteredseals:progression/boss_xp", repeat)

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
        for chapter, (dungeon, _, _) in enumerate(DUNGEONS, 1):
            self.assertIn(f"ss_ngchapter matches {chapter} run return run locate structure shatteredseals:{dungeon}", locate)
        self.assertIn("NEW GAME+",journal.upper())
