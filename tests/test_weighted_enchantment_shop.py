"""Exact weighted rank rolls for all 36 Codex books and all three shop tiers."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "src/main/resources/data/shatteredseals/function"
GIVE = DATA / "enchantments"
RANKS = ((1, 65, "I", 1), (66, 90, "II", 2), (91, 100, "III", 3))


class WeightedEnchantBookTests(unittest.TestCase):
    def test_shop_rolls_once_after_validation_and_resets(self):
        load = (DATA / "load.mcfunction").read_text()
        self.assertIn("scoreboard objectives add ss_ebook_rank dummy", load)
        for tier, cost in (("common", 160), ("rare", 550), ("legendary", 1395)):
            with self.subTest(tier=tier):
                text = (GIVE / f"buy_{tier}.mcfunction").read_text()
                validate = text.index("run return run tellraw")
                enchant_roll = text.index("ss_ebook_roll run random value 1..12")
                rank_roll = text.index("ss_ebook_rank run random value 1..100")
                deduct = text.index(f"xp add @s -{cost} points")
                first_give = text.index("function shatteredseals:enchantments/give_")
                reset = text.index("scoreboard players reset @s ss_ebook_rank")
                self.assertLess(validate, enchant_roll)
                self.assertLess(enchant_roll, rank_roll)
                self.assertLess(rank_roll, deduct)
                self.assertLess(deduct, first_give)
                self.assertLess(first_give, reset)
                self.assertEqual(text.count("ss_ebook_rank run random value 1..100"), 1)
                self.assertEqual(text.count("scoreboard players reset @s ss_ebook_rank"), 1)

    def test_rolls_cover_100_percent_with_the_agreed_odds(self):
        self.assertEqual([b - a + 1 for a, b, _, _ in RANKS], [65, 25, 10])
        self.assertEqual(sum(b - a + 1 for a, b, _, _ in RANKS), 100)
        self.assertEqual(RANKS[0][0], 1)
        self.assertEqual(RANKS[-1][1], 100)
        for preceding, following in zip(RANKS, RANKS[1:]):
            self.assertEqual(preceding[1] + 1, following[0])

    def test_every_custom_book_has_correct_stored_rank_and_receipt(self):
        paths = sorted(GIVE.glob("give_*.mcfunction"))
        self.assertEqual(len(paths), 36)
        for path in paths:
            enchant_id = path.stem.removeprefix("give_")
            lines = path.read_text().splitlines()
            with self.subTest(book=enchant_id):
                self.assertEqual(len([line for line in lines if " run give @s " in line]), 3)
                self.assertEqual(len([line for line in lines if " run tellraw @s " in line]), 3)
                for start, end, roman, rank in RANKS:
                    guard = (f"execute unless score @s ss_ebook_rank matches 66..100"
                             if rank == 1 else
                             f"execute if score @s ss_ebook_rank matches {start}..{end}")
                    book_lines = [line for line in lines if line.startswith(guard + " run give @s ")]
                    receipt_lines = [line for line in lines if line.startswith(guard + " run tellraw @s ")]
                    self.assertEqual(len(book_lines), 1)
                    self.assertEqual(len(receipt_lines), 1)
                    self.assertIn(f'"shatteredseals:{enchant_id}":{rank}', book_lines[0])
                    self.assertIn(f"Level {roman} / III", book_lines[0])
                    self.assertIn(f" {roman} (", receipt_lines[0])
                # With no active purchase rank, the original direct give behavior is Level I.
                self.assertIn("execute unless score @s ss_ebook_rank matches 66..100 run give", lines[1])

    def test_dynamic_tooltip_removes_stale_rank_and_anvil_lore(self):
        text = (ROOT / "src/main/java/com/shatteredseals/client/CodexBookTooltips.java").read_text()
        self.assertIn("entry.getIntValue()", text)
        self.assertIn('text.startsWith("Combine matching books in an anvil to reach")', text)
        self.assertIn('text.startsWith("Maximum enchantment level reached.")', text)


if __name__ == "__main__":
    unittest.main()
