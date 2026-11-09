"""v1.0.58 Sanctuary and Chronicle regression gates."""
from pathlib import Path
import ast
import unittest
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/com/shatteredseals"
class Update158Tests(unittest.TestCase):
    def test_structure_generator_and_archive(self):
        source=(ROOT/"scripts/generate_final_world.py").read_text()
        ast.parse(source)
        for word in ("decorate_sanctuary","ss_sanctuary_chronicle_archive",
                     "blue_stained_glass_pane","deepslate_brick_wall","chiseled_bookshelf"):
            self.assertIn(word,source)
    def test_placeable_safety(self):
        source=(JAVA/"world/SanctuaryPlacement.java").read_text()
        for word in ("hasChunkAt(", "getBlockEntity(", "canBeReplaced(", "MAX_FOUNDATION_DROP",
                     "SSSanctuaryPlaced","placeInWorld(", "stack.shrink(1)"):
            self.assertIn(word,source)
    def test_reward_save_compatibility(self):
        source=(JAVA/"adventure/FinalAdventure.java").read_text()
        for word in ("+ 250","giveExperiencePoints(30)","SSDepthsEliteMask",
                     "SSChronicleComplete","SSSanctuaryKeystoneGranted"):
            self.assertIn(word,source)
    def test_sword_guard_pose_only(self):
        pose=(JAVA/"client/SwordGuardPoseTransform.java").read_text()
        guard=(JAVA/"combat/ClassicSwordGuard.java").read_text()
        self.assertIn("pose.translate(-0.30, 0.42, -0.19)",pose)
        self.assertIn("DAMAGE_MULTIPLIER = 0.60F",guard)
if __name__=="__main__":
    unittest.main()
