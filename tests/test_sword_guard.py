"""Classic sword-guard input and server-side damage mitigation wiring."""
import unittest
from pathlib import Path

JAVA = Path(__file__).resolve().parents[1] / "src/main/java/com/shatteredseals"

class SwordGuardTests(unittest.TestCase):
    def test_input_is_hold_based_and_expires(self):
        client = (JAVA / "client/SkillTreeClient.java").read_text()
        server = (JAVA / "combat/ClassicSwordGuard.java").read_text()
        self.assertIn("mc.options.keyUse.isDown()", client)
        self.assertIn("new SwordGuard(holding)", client)
        self.assertIn("mc.screen == null", client)
        self.assertIn("HOLD_TIMEOUT = 8", server)
        self.assertIn("getGameTime() + HOLD_TIMEOUT", server)
        self.assertIn("getGameTime() >", server)
        self.assertIn("player.getPersistentData().remove(GUARD_UNTIL)", server)

    def test_server_checks_weapon_facing_and_damage_origin(self):
        combat = (JAVA / "combat/ClassicSwordGuard.java").read_text()
        network = (JAVA / "net/SkillNetworking.java").read_text()
        self.assertIn("LivingIncomingDamageEvent", combat)
        self.assertIn("ItemTags.SWORDS", combat)
        self.assertIn("event.getSource().getSourcePosition()", combat)
        self.assertIn("event.getSource().getEntity().position()", combat)
        self.assertIn("isFacingAttack(player.position(), player.getLookAngle(), source)", combat)
        self.assertIn("SoundEvents.SHIELD_BLOCK", combat)
        self.assertIn("return forward >= 0.35", combat)
        self.assertIn("DAMAGE_MULTIPLIER = 0.60F", combat)
        self.assertIn("event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER)", combat)
        self.assertIn("registrar.playToServer(SwordGuard.TYPE", network)

if __name__ == "__main__":
    unittest.main()
