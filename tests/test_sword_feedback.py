"""Regress sword-block pose, defensive sound and dynamic ability key labels."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/com/shatteredseals"

class FeedbackTests(unittest.TestCase):
    def test_first_person_guard_only_while_held(self):
        feedback=(JAVA/"client/SwordGuardFeedback.java").read_text()
        self.assertIn("RenderHandEvent",feedback)
        self.assertIn("InteractionHand.MAIN_HAND",feedback)
        self.assertIn("getPoseStack()",feedback)
        self.assertIn("isSwordGuardHeld(mc)",feedback)
        self.assertIn("ItemTags.SWORDS",feedback)
        self.assertIn('"BLOCK"',feedback)

    def test_dynamic_keybinds(self):
        client=(JAVA/"client/SkillTreeClient.java").read_text()
        hud=(JAVA/"client/CombatHudOverlay.java").read_text()
        self.assertIn("public static KeyMapping abilityKey(int slot)",client)
        for i,n in enumerate(("CAST_ABILITY","CAST_2","CAST_3","CAST_4")):
            self.assertIn("case "+str(i)+" -> "+n,client)
        self.assertIn("getTranslatedKeyMessage().getString()",hud)
        self.assertNotIn('private static final String[] KEYS',hud)

if __name__=="__main__":
    unittest.main()
