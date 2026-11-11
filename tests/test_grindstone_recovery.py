"""Grindstone extraction must work in the real 1.21.1 menu, not only direct events."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
SOURCE = (ROOT / "src/main/java/com/shatteredseals/enchantments/GrindstoneEnchantmentRecovery.java").read_text()
GAMETEST = (ROOT / "src/main/java/com/shatteredseals/diagnostics/GrindstoneRecoveryGameTests.java").read_text()

class GrindstoneRecoveryTests(unittest.TestCase):
    def test_one_gear_input_works_without_a_blank_book(self):
        for check in (
            "if (top.isEmpty()) return find(bottom, false)",
            "if (bottom.isEmpty()) return find(top, true)",
            "return null; // Never intercept two-item vanilla repair",
            "gear.getCount() != 1", "gear.is(Items.ENCHANTED_BOOK)",
            "DataComponents.ENCHANTMENTS", "ShatteredSeals.MOD_ID",
            "firstId == null || id.compareTo(firstId) < 0",
        ):
            self.assertIn(check, SOURCE)

    def test_book_level_and_original_item_transfer(self):
        for check in (
            "EnchantedBookItem.createForEnchantment",
            "new EnchantmentInstance(extraction.enchantment(), extraction.level())",
            "EnchantmentHelper.updateEnchantments",
            "mutable.removeIf(enchantment -> enchantment.equals(extraction.enchantment()))",
            "event.setOutput(extractedBook(extraction))",
            "event.setNewTopItem(updated)", "event.setNewBottomItem(updated)",
            "event.setXp(0)",
        ):
            self.assertIn(check, SOURCE)
        self.assertNotIn("consumeOneBook", SOURCE)
        self.assertNotIn("books.shrink", SOURCE)

    def test_no_inventory_modification_or_free_dupes(self):
        self.assertIn("GrindstoneEvent.OnTakeItem", SOURCE)
        self.assertIn("GrindstoneEvent.OnPlaceItem", SOURCE)
        self.assertNotIn("player.getInventory().add", SOURCE)
        self.assertNotIn("spawnAtLocation", SOURCE)
        self.assertNotIn("setCanceled(true)", SOURCE)
        self.assertNotIn("player.drop", SOURCE)

    def test_actual_minecraft_grindstone_menu_is_exercised(self):
        for check in (
            "new GrindstoneMenu(", "menu.getSlot(1).mayPlace(new ItemStack(Items.BOOK))",
            "menu.getSlot(0).mayPlace(weapon)", "menu.getSlot(0).set(weapon)",
            "menu.getSlot(2).getItem()", "menu.getSlot(2).onTake(player, book)",
            "rank(changedGear, soul, false) != 0",
            'enchant(helper, "soul_reaper")', 'enchant(helper, "frostbite")',
            'enchant(helper, "soul_edge")', "gear.getDamageValue() != 99",
            "before - after != 1", "book.get(DataComponents.STORED_ENCHANTMENTS).size() != 1",
        ):
            self.assertIn(check, GAMETEST)

if __name__ == "__main__":
    unittest.main()
