"""v1.0.58 Sanctuary and Chronicle regression gates."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/com/shatteredseals"
class Update158Tests(unittest.TestCase):
    def test_placeable_safety(self):
        source=(JAVA/"world/SanctuaryPlacement.java").read_text()
        for word in ("hasChunkAt(", "getBlockEntity(", "canBeReplaced(", "MAX_FOUNDATION_DROP",
                     "SSSanctuaryPlaced","placeInWorld(", "stack.shrink(1)",
                     "SSSanctuaryConfirmPos", "SSSanctuaryConfirmTick", "sendParticles(",
                     "canClear(state)", "replaced.entrySet()", "toClear.add(pos)",
                     "MAX_FOUNDATION_DROP = 32", "getWorldBorder()"):
            self.assertIn(word,source)
    def test_reward_save_compatibility(self):
        source=(JAVA/"adventure/FinalAdventure.java").read_text()
        for word in ("ChronicleRewards.eliteXp(player)","giveExperiencePoints(30)","SSDepthsEliteMask",
                     "SSChronicleComplete","SSSanctuaryKeystoneGranted"):
            self.assertIn(word,source)
        rewards=(JAVA/"adventure/ChronicleRewards.java").read_text()
        self.assertIn("return 250 + (wearingCrown(player) ? ELITE_BONUS_XP : 0)",rewards)
    def test_generated_original_assets(self):
        from json import loads
        sounds=loads((ROOT/"src/main/resources/assets/shatteredseals/sounds.json").read_text())
        self.assertEqual(set(sounds),{"music.boss","music.explore","music.sanctuary"})
        for name in ("boss","explore","sanctuary"):
            self.assertTrue((ROOT/f"src/main/resources/assets/shatteredseals/sounds/music/{name}.ogg").is_file())
        self.assertIn("registerEntityRenderer(EntityType.RAVAGER", (ROOT/"src/main/java/com/shatteredseals/client/BossSkinRenderers.java").read_text())
        for name in ("smuggler_king","cinder_warden","hollow_knight",
                     "arcane_seer","sixfold_guardian","hollow_king"):
            self.assertTrue((ROOT/f"src/main/resources/assets/shatteredseals/textures/item/boss_mask_{name}.png").is_file())
            self.assertTrue((ROOT/f"src/main/resources/assets/shatteredseals/textures/entity/boss/{name}.png").is_file())
            self.assertTrue((ROOT/f"src/main/resources/assets/shatteredseals/models/item/boss_mask_{name}.json").is_file())
    def test_sword_guard_pose_only(self):
        pose=(JAVA/"client/SwordGuardPoseTransform.java").read_text()
        guard=(JAVA/"combat/ClassicSwordGuard.java").read_text()
        self.assertIn("pose.translate(-0.30, 0.42, -0.19)",pose)
        self.assertIn("DAMAGE_MULTIPLIER = 0.60F",guard)
if __name__=="__main__":
    unittest.main()
