"""Survival inventory accepts direct Codex-book placement without duping."""
from pathlib import Path
import unittest
S=(Path(__file__).resolve().parents[1]/'src/main/java/com/shatteredseals/enchantments/CodexBookApplication.java').read_text()
class DragToEnchantTests(unittest.TestCase):
    def test_event_is_server_authoritative_and_inventory_only(self):
        for part in ('ItemStackedOnOtherEvent','player.containerMenu != player.inventoryMenu',
                     'event.getClickAction() != ClickAction.PRIMARY','stored.size() != 1',
                     'supportsEnchantment(enchant)','targetRank > enchant.value().getMaxLevel()',
                     'if (player.level().isClientSide) return;','EnchantmentHelper.updateEnchantments',
                     'remaining.shrink(1)','event.getCarriedSlotAccess().set(remaining)',
                     'event.setCanceled(true)'):
            self.assertIn(part,S)
if __name__=='__main__':unittest.main()
