"""Custom Shattered Seals enchantments can be recovered from a grindstone."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
SOURCE = (ROOT / "src/main/java/com/shatteredseals/enchantments/GrindstoneEnchantmentRecovery.java").read_text()
GAMETEST = (ROOT / "src/main/java/com/shatteredseals/diagnostics/GrindstoneRecoveryGameTests.java").read_text()

class GrindstoneRecoveryTests(unittest.TestCase):
    def test_only_book_plus_gear_activates_extraction(self):
        for check in (
            "top.is(Items.BOOK)", "bottom.is(Items.BOOK)",
            "gear.getCount() != 1", "gear.is(Items.ENCHANTED_BOOK)",
            "DataComponents.ENCHANTMENTS", "ShatteredSeals.MOD_ID",
            "firstId == null || id.compareTo(firstId) < 0",
        ):
            self.assertIn(check, SOURCE)

    def test_book_level_and_actual_enchantment_transfer(self):
        for check in (
            "EnchantedBookItem.createForEnchantment",
            "new EnchantmentInstance(extraction.enchantment(), extraction.level())",
            "EnchantmentHelper.updateEnchantments",
            "mutable.removeIf(enchantment -> enchantment.equals(extraction.enchantment()))",
            "remaining.shrink(1)", "event.setOutput(extractedBook(extraction))",
            "event.setNewTopItem(changedTool)", "event.setNewBottomItem(changedTool)",
            "event.setXp(0)",
        ):
            self.assertIn(check, SOURCE)

    def test_no_side_channel_or_free_enchant_dupes(self):
        self.assertIn("GrindstoneEvent.OnTakeItem", SOURCE)
        self.assertIn("GrindstoneEvent.OnPlaceItem", SOURCE)
        self.assertNotIn("player.getInventory().add", SOURCE)
        self.assertNotIn("spawnAtLocation", SOURCE)
        self.assertNotIn("setCanceled(true)", SOURCE)
        self.assertNotIn("player.drop", SOURCE)

    def test_real_game_suite_covers_real_registry_levels_both_slots(self):
        for check in (
            'enchant(helper, "soul_reaper")', 'enchant(helper, "frostbite")',
            'enchant(helper, "soul_edge")', "getDamageValue() != 99",
            "beforeCount - afterCount != 1", "result.get(DataComponents.STORED_ENCHANTMENTS).size() != 1",
            "preview.getXp() != 0", "getNewTopItem().getCount() != 3",
            "GrindstoneEvent.OnPlaceItem(tool, ItemStack.EMPTY, -1)",
        ):
            self.assertIn(check, GAMETEST)

if __name__ == "__main__":
    unittest.main()
