"""Combat HUD geometry must keep ability panels off the Minecraft hotbar."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
CLIENT = ROOT / "src/main/java/com/shatteredseals/client"

class CombatHudTests(unittest.TestCase):
    def test_two_buttons_on_each_hotbar_side(self):
        layout=(CLIENT/"CombatHudLayout.java").read_text()
        overlay=(CLIENT/"CombatHudOverlay.java").read_text()
        self.assertIn("slot < 2 ? leftX : rightX",layout)
        self.assertIn("slot % 2 == 0 ? firstY : secondY",layout)
        self.assertIn("HOTBAR_HALF_WIDTH = 91",layout)
        self.assertIn("int panelWidth = Math.max(0, Math.min(MAX_PANEL_WIDTH",layout)
        self.assertIn("CombatHudLayout.measure(g.guiWidth(), g.guiHeight())",overlay)
        self.assertIn("drawAbility(g, mc, layout, classIndex, i",overlay)
        self.assertNotIn("width * 4 + gap * 3",overlay)

    def test_sidepanels_fit_common_viewports(self):
        # Mirror the small pure-Java layout calculation to exercise the
        # hotbar bounds for the screenshot-sized 315px GUI and larger screens.
        for width in (250, 300, 315, 360, 427, 640, 960):
            with self.subTest(width=width):
                center=width//2
                hotbar_left=center-91
                hotbar_right=center+91
                panel_width=max(0,min(54,min(hotbar_left-5-4,width-hotbar_right-5-4)))
                left_x=hotbar_left-5-panel_width
                right_x=hotbar_right+5
                self.assertGreaterEqual(left_x,4)
                self.assertLessEqual(left_x+panel_width,hotbar_left-5)
                self.assertGreaterEqual(right_x,hotbar_right+5)
                self.assertLessEqual(right_x+panel_width,width-4)

    def test_status_is_aligned_above_survival_hearts_hunger(self):
        java=(CLIENT/"CombatHudLayout.java").read_text()
        overlay=(CLIENT/"CombatHudOverlay.java").read_text()
        self.assertIn("viewportHeight - 58",java)
        self.assertIn("viewportHeight - 47",java)
        self.assertIn("(left + right - mc.font.width(amount)) / 2",overlay)
        self.assertNotIn("CLASS_NAMES[classIndex]",overlay)
        self.assertIn("g.fill(left, barY, right, barY + 5",overlay)
        self.assertIn("mc.font.plainSubstrByWidth",overlay)
        self.assertNotIn('private static final String[] KEYS',overlay)
        self.assertIn('SkillTreeClient.abilityKey(slot).getTranslatedKeyMessage().getString()',overlay)
        self.assertIn("mc.options.hideGui",overlay)

    def test_action_bar_and_survival_status_have_breathing_room(self):
        # Minecraft's standard actionbar message starts at H-68 with a 9px font.
        # The native class/resource label starts at H-58 and is also 9px tall.
        # The five-pixel resource bar ends at H-42; vanilla hearts begin at H-39.
        gui_height=210
        actionbar_y=gui_height-68
        title_y=gui_height-58
        bar_y=gui_height-47
        hearts_y=gui_height-39
        self.assertGreaterEqual(title_y, actionbar_y+9+1)
        self.assertGreaterEqual(bar_y, title_y+9+2)
        self.assertLessEqual(bar_y+5, hearts_y-3)


if __name__ == "__main__":
    unittest.main()
