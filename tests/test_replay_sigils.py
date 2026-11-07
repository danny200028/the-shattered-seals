"""Replay loops must not softlock when the original sigil chest is empty."""
import json
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
BOSSES=["smuggler_king","cinder_warden","hollow_knight","arcane_seer","sixfold_guardian","hollow_king"]

class ReplaySigilTests(unittest.TestCase):
    def test_replay_resets_all_chapter_puzzles_and_quests(self):
        source=(DATA/"function/ngplus/start.mcfunction").read_text()
        for n in range(1,7):
            with self.subTest(chapter=n):
                self.assertIn(f"scoreboard players set @s ss_rune{n} 0",source)
                self.assertIn(f"scoreboard players set @s ss_side{n} 0",source)

    def test_replay_sigil_has_only_the_original_guaranteed_sigil(self):
        for boss in BOSSES:
            with self.subTest(boss=boss):
                loot=json.loads((DATA/f"loot_table/ritual_replay/{boss}.json").read_text())
                self.assertEqual(len(loot["pools"]),1)
                self.assertEqual(loot["pools"][0]["rolls"],1)
                self.assertEqual(len(loot["pools"][0]["entries"]),1)
                entry=loot["pools"][0]["entries"][0]
                self.assertEqual(entry["name"],"minecraft:carrot_on_a_stick")
                data=next(f for f in entry["functions"] if f["function"]=="minecraft:set_custom_data")
                self.assertEqual(data["tag"]["ss_ritual"],boss)

    def test_runes_are_mandatory_for_all_bosses(self):
        for n,boss in enumerate(BOSSES,1):
            ritual=(DATA/f"function/ritual/use_{boss}.mcfunction").read_text()
            self.assertIn(f"execute unless score @s ss_rune{n} matches 3 run return",ritual)

    def test_keeper_awards_only_while_completing_eligible_replay(self):
        src=(ROOT/"src/main/java/com/shatteredseals/adventure/DungeonAdventure.java").read_text()
        self.assertIn('case 2 -> {',src)
        self.assertIn('DungeonChapter.score(player, "ss_ng") >= 1',src)
        self.assertIn('DungeonChapter.score(player, "ss_ngchapter") == chapter.number()',src)
        self.assertIn('"loot give @s loot " + chapter.replaySigilLoot()',src)
        self.assertIn('DungeonChapter.setScore(player, chapter.sideScore(), 3)',src)

if __name__=="__main__":
    unittest.main()
