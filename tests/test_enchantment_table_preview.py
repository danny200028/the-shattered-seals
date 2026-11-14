"""Table previews must mirror server enchant RNG and expose all three outcomes without spending XP."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/shatteredseals"
SERVER = (JAVA / "enchantments/TableEnchantmentPreviewSender.java").read_text()
SCREEN = (JAVA / "client/EnchantmentPreviewOverlay.java").read_text()
NETWORK = (JAVA / "net/SkillNetworking.java").read_text()
PACKET = (JAVA / "net/TableEnchantPreview.java").read_text()


class TablePreviewTests(unittest.TestCase):
    def test_server_authoritative_vanilla_algorithm(self):
        for expected in ("PlayerTickEvent.Post", "ServerPlayer", "EnchantmentMenu",
                         "menu.getEnchantmentSeed() + slot", "EnchantmentHelper.selectEnchantment(",
                         "EnchantmentTags.IN_ENCHANTING_TABLE", "costs[slot]",
                         "item.is(Items.BOOK) && chosen.size() > 1",
                         "chosen.remove(random.nextInt(chosen.size()))",
                         "Enchantment.getFullname(entry.enchantment, entry.level)",
                         "ItemStack.matches(item, current)", "Arrays.equals(costs, menu.costs)"):
            with self.subTest(contract=expected): self.assertIn(expected, SERVER)
        self.assertIn("for (int slot = 0; slot < 3; slot++)", SERVER)
        self.assertIn("LAST.remove(player)", SERVER)
        self.assertIn("PacketDistributor.sendToPlayer(player", SERVER)
        self.assertNotIn("xp add", SERVER)
        self.assertNotIn("clickMenuButton", SERVER)

    def test_client_shows_all_enchants_with_levels_on_hover(self):
        for expected in ("ScreenEvent.Render.Post", "EnchantmentScreen",
                         "preview.menuId() != menu.containerId",
                         "preview.itemId() != Item.getId",
                         "menu.costs[i] != preview.costs()[i]",
                         "Will enchant with:", 'names.split("\\n")',
                         "rowY = top + 14 + slot * 19",
                         "mouseX < left + 60", "mouseX >= left + 168",
                         "renderTooltip(mc.font, lines", "Uses "):
            with self.subTest(contract=expected): self.assertIn(expected, SCREEN)

    def test_packet_is_one_way_and_bounded(self):
        self.assertIn("registrar.playToClient(TableEnchantPreview.TYPE", NETWORK)
        self.assertNotIn("registrar.playToServer(TableEnchantPreview.TYPE", NETWORK)
        for expected in ("writeUtf(packet.enchantments()[i], 4096)",
                         "buf.readUtf(4096)", "for (int i = 0; i < 3; i++)",
                         "menuId", "itemId", "seed", "int[] costs"):
            self.assertIn(expected, PACKET)


if __name__ == "__main__":
    unittest.main()
