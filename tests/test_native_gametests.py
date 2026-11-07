"""Six native NeoForge GameTests must be hooked into a real game server run."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
BOSSES=("smugglersDen","cinderCitadel","hollowCrypt","arcaneObservatory","sixfoldTemple","hollowKingsFortress")
TEMPLATES=("smugglers_den","cinder_citadel","hollow_crypt","arcane_observatory","sixfold_temple","hollow_kings_fortress")
class NativeMinecraftGameTests(unittest.TestCase):
    def test_six_real_game_test_templates(self):
        src=(ROOT/"src/main/java/com/shatteredseals/diagnostics/DungeonGameTests.java").read_text()
        self.assertIn("@GameTestHolder(ShatteredSeals.MOD_ID)",src)
        self.assertIn("@PrefixGameTestTemplate(false)",src)
        for name,template in zip(BOSSES,TEMPLATES):
            with self.subTest(template=template):
                self.assertIn('template = "'+template+'"',src)
                self.assertIn("void "+name+"(GameTestHelper helper)",src)
        self.assertIn("helper.assertBlockPresent(Blocks.CRYING_OBSIDIAN",src)
        self.assertIn("RuntimeAudit.replaySigilRolls",src)

    def test_ci_runs_real_game_server_not_just_static_parser(self):
        gradle=(ROOT/"build.gradle").read_text()
        ci=(ROOT/".github/workflows/in-game-tests.yml").read_text()
        self.assertIn("gameTestServer",gradle)
        self.assertIn("type = 'gameTestServer'",gradle)
        self.assertIn("runGameTestServer",ci)
        self.assertIn("gradle --no-daemon runGameTestServer",ci)
        self.assertIn("scripts/generate_dungeons.py",ci)
if __name__=="__main__":
    unittest.main()
