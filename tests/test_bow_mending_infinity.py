"""Mending + Infinity must work on bows, without affecting other vanilla conflicts."""
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
TAGS = ROOT / "src/main/resources/data/minecraft/tags/enchantment/exclusive_set"
GAME_TEST = ROOT / "src/main/java/com/shatteredseals/diagnostics/BowMendingInfinityGameTests.java"


class BowMendingInfinityTests(unittest.TestCase):
    def test_only_bow_exclusion_is_removed(self):
        self.assertEqual(json.loads((TAGS / "bow.json").read_text()),
                         {"replace": True, "values": []})
        self.assertEqual(sorted(p.name for p in TAGS.glob("*.json")), ["bow.json"])

    def test_no_vanilla_enchantment_definitions_overwritten(self):
        vanilla = ROOT / "src/main/resources/data/minecraft/enchantment"
        for name in ("mending.json", "infinity.json"):
            self.assertFalse((vanilla / name).exists())

    def test_real_minecraft_gametest_checks_both_anvil_orders(self):
        text = GAME_TEST.read_text()
        for expected in (
            "@GameTestHolder(ShatteredSeals.MOD_ID)",
            "@GameTest(template = \"smugglers_den\"",
            "Enchantment.areCompatible(infinity, mending)",
            "Enchantment.areCompatible(mending, infinity)",
            "EnchantmentHelper.isEnchantmentCompatible(List.of(infinity), mending)",
            "EnchantmentHelper.isEnchantmentCompatible(List.of(mending), infinity)",
            "bow.supportsEnchantment(infinity)",
            "bow.supportsEnchantment(mending)",
            "mutable.set(infinity, 1)",
            "mutable.set(mending, 1)",
            "applied.getLevel(infinity) != 1",
            "applied.getLevel(mending) != 1",
            "Enchantment.areCompatible(sharpness, smite)",
            "Enchantment.areCompatible(fortune, silkTouch)",
            "helper.succeed()",
        ):
            with self.subTest(contract=expected):
                self.assertIn(expected, text)


if __name__ == "__main__":
    unittest.main()
