"""Duplicate Codex book salvage only refunds after a server-validated exact duplicate."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = (ROOT / "src/main/java/com/shatteredseals/enchantments/DuplicateBookRecycling.java").read_text()
GUI = (ROOT / "src/main/java/com/shatteredseals/client/AdventureScreen.java").read_text()
LOAD = (ROOT / "src/main/resources/data/shatteredseals/function/load.mcfunction").read_text()
TICK = (ROOT / "src/main/resources/data/shatteredseals/function/tick.mcfunction").read_text()
GAMETEST = (ROOT / "src/main/java/com/shatteredseals/diagnostics/DuplicateBookRecyclingGameTests.java").read_text()
BUY = ROOT / "src/main/resources/data/shatteredseals/function/enchantments"


class DuplicateCodexBookTests(unittest.TestCase):
    def test_refunds_half_of_each_shop_tier_and_cover_every_book(self):
        tiers = (("COMMON", "common", 80), ("RARE", "rare", 275),
                 ("LEGENDARY", "legendary", 698))
        all_ids = []
        for upper, lower, refund in tiers:
            with self.subTest(tier=lower):
                self.assertIn(f"{upper}_REFUND = {refund}", JAVA)
                found = set(re.findall(r"give_([a-z_]+)", (BUY / f"buy_{lower}.mcfunction").read_text()))
                self.assertEqual(len(found), 12)
                body = re.search(rf"{upper} = Set.of\((.*?)\);", JAVA, re.DOTALL)
                self.assertIsNotNone(body)
                names = set(re.findall(r'"([a-z_]+)"', body.group(1)))
                self.assertEqual(found, names)
                all_ids.extend(names)
        self.assertEqual(len(set(all_ids)), 36)

    def test_server_only_atomic_safety_and_mainhand(self):
        for fragment in (
            "@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)",
            "PlayerTickEvent.Post", 'getObjective("ss_ebook_recycle")',
            "getOrCreatePlayerScore(player, objective).set(0)",
            "player.getMainHandItem()", "held.shrink(1)", "player.giveExperiencePoints(refund)",
            "DataComponents.STORED_ENCHANTMENTS", "stored.size() != 1",
            "getNamespace().equals(ShatteredSeals.MOD_ID)",
            "entry.getIntValue() > 3", "requested.equals(identify(candidate))",
            "copies >= 2", "player.isCreative()", "player.isSpectator()",
            "if (!hasDuplicate(player.getInventory(), key)) return Result.NO_DUPLICATE",
        ):
            with self.subTest(fragment=fragment): self.assertIn(fragment, JAVA)
        self.assertLess(JAVA.index("copies >= 2"), JAVA.index("held.shrink(1)"))
        self.assertLess(JAVA.index("held.shrink(1)"), JAVA.index("player.giveExperiencePoints(refund)"))

    def test_codex_uses_confirmation_and_server_trigger(self):
        self.assertIn("recycleArmed", GUI)
        self.assertIn('perform("ss_ebook_recycle", 1)', GUI)
        self.assertIn("CONFIRM: Recycle held book", GUI)
        self.assertIn("same enchant + level", GUI)
        self.assertIn("scoreboard objectives add ss_ebook_recycle trigger", LOAD)
        self.assertIn("scoreboard players enable @a ss_ebook_recycle", TICK)

    def test_real_game_tests_cover_repeat_and_mismatched_levels(self):
        for fragment in ("@GameTestHolder(ShatteredSeals.MOD_ID)",
                         "duplicateCommonRefundsAndKeepsSecondBook",
                         "uniqueAndWrongRankNeverConsumeBooks",
                         "vanillaBooksAndNonBookItemsNeverRecycle",
                         "helper.succeed()", "player.totalExperience - beforeXp"):
            with self.subTest(fragment=fragment): self.assertIn(fragment, GAMETEST)


if __name__ == "__main__":
    unittest.main()
