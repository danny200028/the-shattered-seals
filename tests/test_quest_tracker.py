"""Quest tracker wiring checks (static only; Minecraft runtime still needs testing)."""
import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/shatteredseals/client"
LANG = ROOT / "src/main/resources/assets/shatteredseals/lang/en_us.json"

class QuestTrackerTests(unittest.TestCase):
    def test_toggle_key_is_registered_and_consumed(self):
        client = (JAVA / "SkillTreeClient.java").read_text()
        self.assertIn("GLFW.GLFW_KEY_L", client)
        self.assertIn("event.register(OPEN_QUEST_TRACKER)", client)
        self.assertIn("while (OPEN_QUEST_TRACKER.consumeClick()) QuestTrackerOverlay.toggle()", client)
        self.assertIn("key.shatteredseals.toggle_quest_tracker", json.loads(LANG.read_text()))

    def test_tracker_is_read_only_and_tracks_replays(self):
        overlay = (JAVA / "QuestTrackerOverlay.java").read_text()
        self.assertIn("RenderGuiEvent.Post", overlay)
        self.assertIn("ClientSkillState.current()", overlay)
        self.assertIn("state.replayChapter()", overlay)
        self.assertIn("state.bossWins()", overlay)
        self.assertIn("state.puzzleStep()", overlay)
        self.assertIn("state.sideQuestState()", overlay)
        self.assertIn("mc.options.hideGui", overlay)
        self.assertNotRegex(overlay, r"(sendCommand|PacketDistributor|setScore|performPrefixedCommand)")
        self.assertIn("isVisible()", overlay)

if __name__ == "__main__":
    unittest.main()
