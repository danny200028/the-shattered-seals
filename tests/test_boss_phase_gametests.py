"""Native GameTests must execute boss phase datapack functions in Minecraft."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "src/main/java/com/shatteredseals/diagnostics/BossPhaseGameTests.java"
BOSSES = (
    ("smugglerPhases", "smugglers_den", "PILLAGER"),
    ("cinderPhases", "cinder_citadel", "HUSK"),
    ("hollowPhases", "hollow_crypt", "WITHER_SKELETON"),
    ("arcanePhases", "arcane_observatory", "EVOKER"),
    ("sixfoldPhases", "sixfold_temple", "RAVAGER"),
    ("kingPhases", "hollow_kings_fortress", "RAVAGER"),
)
class BossPhaseIntegrationTests(unittest.TestCase):
    def test_six_boss_integration_test_templates(self):
        src=SOURCE.read_text()
        self.assertIn("@GameTestHolder(ShatteredSeals.MOD_ID)",src)
        for method, template, mob in BOSSES:
            with self.subTest(template=template):
                self.assertIn('template = "'+template+'"',src)
                self.assertIn("void "+method+"(GameTestHelper helper)",src)
                self.assertIn("EntityType."+mob,src)
    def test_real_phases_and_cleanup_exercised(self):
        src=SOURCE.read_text()
        for needle in (
            'runAsBoss(level, boss, bossName, "phase_two")',
            'runAsBoss(level, boss, bossName, "phase_three")',
            'ss_phase2', 'ss_phase3', 'count != 2', 'count != 3', 'count != 0',
            'boss.discard()', '"function shatteredseals:boss/minion_cleanup"',
            'performPrefixedCommand(', 'ss_minion_',
        ):
            self.assertIn(needle,src)
    def test_ci_still_runs_real_game_server(self):
        workflow=(ROOT/".github/workflows/in-game-tests.yml").read_text()
        self.assertIn("runGameTestServer",workflow)

if __name__ == "__main__":
    unittest.main()
