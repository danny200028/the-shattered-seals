"""Book-on-sword and sword-on-book both work in survival inventory."""
from pathlib import Path
import unittest
S=(Path(__file__).resolve().parents[1]/'src/main/java/com/shatteredseals/enchantments/CodexBookApplication.java').read_text()
class DragToEnchantTests(unittest.TestCase):
    def test_event_is_server_authoritative_and_inventory_only(self):
        for part in ('ItemStackedOnOtherEvent','player.containerMenu != player.inventoryMenu',
                     'event.getClickAction() != ClickAction.PRIMARY','stored.size() != 1',
                     'gear.supportsEnchantment(enchant)',
                     'targetRank > enchant.value().getMaxLevel()',
                     'if (player.level().isClientSide) return;',
                     'EnchantmentHelper.updateEnchantments',
                     'remaining.shrink(1)','event.getCarriedSlotAccess().set(remaining)',
                     'event.setCanceled(true)'):
            self.assertIn(part,S)
    def test_reversed_click_and_incompatible_book_handling(self):
        for part in ('bookOnCursor = cursor.is(Items.ENCHANTED_BOOK)',
                     'bookInSlot = slotted.is(Items.ENCHANTED_BOOK)',
                     'bookOnCursor ? cursor : slotted','bookOnCursor ? slotted : cursor',
                     'event.getSlot().set(upgraded);','event.getCarriedSlotAccess().set(upgraded);',
                     'event.getSlot().set(remaining);','player.isCreative()',
                     'That enchantment cannot be applied to this item.'):
            self.assertIn(part,S)
if __name__=="__main__":unittest.main()
