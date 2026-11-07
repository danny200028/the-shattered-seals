"""Ensure new runtime guard math GameTest is included in the 13-test gate."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
class GuardGameTestChecks(unittest.TestCase):
    def test_native_direction_gametest(self):
        java=(ROOT/"src/main/java/com/shatteredseals/diagnostics/SwordGuardGameTest.java").read_text()
        self.assertIn("@GameTestHolder(ShatteredSeals.MOD_ID)",java)
        self.assertIn('@GameTest(template = "smugglers_den"',java)
        self.assertIn("ClassicSwordGuard.isFacingAttack",java)
    def test_count_gate_thirteen(self):
        gate=(ROOT/"scripts/verify_gametest_results.py").read_text()
        self.assertIn("MINIMUM_TESTS = 13",gate)
if __name__=="__main__":
    unittest.main()
