"""Enchanting tooltip replaces vanilla clue; real rolls can contain multiple enchantments."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/shatteredseals"
SERVER = (JAVA / "enchantments/TableEnchantmentPreviewSender.java").read_text()
SCREEN = (JAVA / "client/EnchantmentPreviewOverlay.java").read_text()
NETWORK = (JAVA / "net/SkillNetworking.java").read_text()
PACKET = (JAVA / "net/TableEnchantPreview.java").read_text()
GAMETEST = (JAVA / "diagnostics/VanillaEnchantmentPreviewGameTests.java").read_text()


class TablePreviewTests(unittest.TestCase):
    def test_server_authoritative_vanilla_algorithm(self):
        for expected in ("PlayerTickEvent.Post", "ServerPlayer", "EnchantmentMenu",
                         "enchantmentSeed + slot", "EnchantmentHelper.selectEnchantment(",
                         "EnchantmentTags.IN_ENCHANTING_TABLE", "cost",
                         "item.is(Items.BOOK) && chosen.size() > 1",
                         "chosen.remove(random.nextInt(chosen.size()))",
                         "Enchantment.getFullname(entry.enchantment, entry.level)",
                         "ItemStack.matches(item, current)", "Arrays.equals(costs, menu.costs)",
                         "selectVanillaEnchantments("):
            with self.subTest(contract=expected): self.assertIn(expected, SERVER)
        self.assertIn("for (int slot = 0; slot < 3; slot++)", SERVER)
        self.assertIn("LAST.remove(player)", SERVER)
        self.assertIn("PacketDistributor.sendToPlayer(player", SERVER)
        self.assertNotIn("xp add", SERVER)
        self.assertNotIn("clickMenuButton", SERVER)

    def test_single_enchant_tooltip_no_overlap(self):
        for expected in ("RenderTooltipEvent.GatherComponents", "event.getItemStack().isEmpty()",
                         "preview.menuId() != menu.containerId", "preview.itemId() != Item.getId",
                         "menu.costs[i] != preview.costs()[i]",
                         "EnchantmentScreen", "getGuiScaledWidth()", "getGuiScaledHeight()",
                         "Enchantments (", "Only one enchantment rolled.",
                         "getTooltipElements()", "lines.clear()", "Either.left(",
                         "event.setMaxWidth", "Requires level", "Costs "):
            with self.subTest(contract=expected): self.assertIn(expected, SCREEN)
        self.assertNotIn("ScreenEvent.Render.Post", SCREEN)
        self.assertNotIn("renderTooltip(", SCREEN)
        self.assertNotIn("GuiGraphics", SCREEN)

    def test_real_gametest_checks_multi_enchantment_rolls(self):
        self.assertIn("GameTestHolder", GAMETEST)
        self.assertIn("selectVanillaEnchantments(", GAMETEST)
        self.assertIn("chosen.size() > 1", GAMETEST)
        self.assertIn("helper.succeed()", GAMETEST)

    def test_packet_is_one_way_and_bounded(self):
        self.assertIn("registrar.playToClient(TableEnchantPreview.TYPE", NETWORK)
        self.assertNotIn("registrar.playToServer(TableEnchantPreview.TYPE", NETWORK)
        for expected in ("writeUtf(packet.enchantments()[i], 4096)",
                         "buf.readUtf(4096)", "for (int i = 0; i < 3; i++)",
                         "menuId", "itemId", "seed", "int[] costs"):
            self.assertIn(expected, PACKET)


if __name__ == "__main__":
    unittest.main()
