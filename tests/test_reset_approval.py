"""Ensure reset confirmations expire and GUI cancellation revokes server consent."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FUNCTIONS = ROOT / "src/main/resources/data/shatteredseals/function"

class ResetApprovalTests(unittest.TestCase):
    def test_expiring_server_confirmation(self):
        load = (FUNCTIONS / "load.mcfunction").read_text()
        init = (FUNCTIONS / "progression/init.mcfunction").read_text()
        tick = (FUNCTIONS / "progression/tick.mcfunction").read_text()
        reset = (FUNCTIONS / "progression/reset.mcfunction").read_text()
        confirm = (FUNCTIONS / "progression/reset_confirm.mcfunction").read_text()
        self.assertIn("scoreboard objectives add ss_reset_timer dummy", load)
        self.assertIn("scoreboard players set @s ss_reset_timer 0", init)
        self.assertIn("scoreboard players set @s ss_reset_timer 1200", reset)
        self.assertIn("scores={ss_reset_timer=1..}", tick)
        self.assertIn("scores={ss_reset_timer=0,ss_reset_confirm=1..}", tick)
        self.assertIn("unless score @s ss_reset_timer matches 1.. run return", confirm)
        self.assertIn("ss_respec matches 3 run scoreboard players set @s ss_reset_confirm 0", reset)
        self.assertIn("ss_respec matches 3 run scoreboard players set @s ss_reset_timer 0", reset)
        self.assertNotIn("clear @s minecraft:dragon_egg", reset)

    def test_cancel_button_and_close_revoke_permission(self):
        ui = (ROOT/"src/main/java/com/shatteredseals/client/SkillTreeScreen.java").read_text()
        self.assertGreaterEqual(ui.count('perform("ss_respec", 3)'), 2)
        self.assertIn("if (confirmingReset)",ui)
        self.assertNotIn("clear @s minecraft:dragon_egg",ui)

if __name__ == "__main__":
    unittest.main()
