"""Client menu text must stay crisp and opaque after Minecraft blur rendering."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
CLIENT = ROOT / "src/main/java/com/shatteredseals/client"


class SharpMenuTests(unittest.TestCase):
    def test_text_is_drawn_after_widgets_not_before(self):
        for name in ("SkillTreeScreen.java", "AdventureScreen.java"):
            with self.subTest(screen=name):
                source = (CLIENT / name).read_text()
                section = source.split("@Override public void render(GuiGraphics graphics", 1)[1].split("\n    }", 1)[0]
                self.assertLess(section.find("renderBackground(graphics"), section.find("super.render(graphics"))
                self.assertLess(section.find("super.render(graphics"), section.find("graphics.drawCenteredString(font, title"))
                self.assertEqual(section.count("super.render(graphics, mouseX, mouseY, delta)"), 1)

    def test_menu_labels_all_have_explicit_opaque_argb_colours(self):
        for name in ("SkillTreeScreen.java", "AdventureScreen.java"):
            with self.subTest(screen=name):
                source = (CLIENT / name).read_text()
                self.assertFalse(re.search(r"\b0x[0-9a-fA-F]{6}\b", source),
                                 "Menus must not pass ambiguous six-digit colours to Minecraft fonts")
                self.assertIn("0xFF", source)

    def test_skill_tree_preserves_all_server_owned_button_actions(self):
        source=(CLIENT / "SkillTreeScreen.java").read_text()
        for expected in ('perform("ss_branch", number)', 'perform("ss_ascend", number)',
                         'perform("ss_buy", selectedClass + 1)', 'perform("ss_switch", selectedClass+1)',
                         'perform("ss_respec", 1)', 'perform("ss_respec", 2)',
                         'perform("ss_respec", 3)'):
            with self.subTest(command=expected):
                self.assertIn(expected, source)


if __name__ == "__main__":
    unittest.main()
