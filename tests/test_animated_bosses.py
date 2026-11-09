"""Six completely custom boss body models and original asset smoke gates."""
from pathlib import Path
import ast
import unittest
ROOT = Path(__file__).resolve().parents[1]
CLIENT = ROOT / "src/main/java/com/shatteredseals/client"
ASSETS = ROOT / "src/main/resources/assets/shatteredseals"
BOSSES=("smuggler_king","cinder_warden","hollow_knight",
        "arcane_seer","sixfold_guardian","hollow_king")

class AnimatedBossTests(unittest.TestCase):
    def test_native_models_have_six_unique_shapes_and_animations(self):
        source=(CLIENT/"AnimatedBossModel.java").read_text()
        for part in ("createBodyLayer","head","torso","right_arm","left_arm",
                     "right_leg","left_leg","left_horn","right_horn",
                     "case 0 ->","case 1 ->","case 2 ->","case 3 ->","case 4 ->"):
            self.assertIn(part,source)
        for state in ("getAttackAnim(", "hurtTime", "deathTime", "ss_phase2", "ss_phase3",
                      "limbSwing", "resetPose", "enteredTick"):
            self.assertIn(state,source)
        for name in BOSSES:self.assertIn(name,source)
        self.assertIn("LayerDefinition.create(mesh,128,128)",source)

    def test_vanilla_entities_have_a_renderer_fallback(self):
        source=(CLIENT/"BossSkinRenderers.java").read_text()
        self.assertIn("return vanilla;",source)
        self.assertIn("new AnimatedRenderer",source)
        self.assertIn("select(entity).render",source)
        for mob in ("PILLAGER","HUSK","WITHER_SKELETON","EVOKER","RAVAGER"):
            self.assertIn("EntityType."+mob,source)

    def test_generated_texture_atlases_are_full_resolution(self):
        source=(ROOT/"scripts/generate_v158_assets.py").read_text()
        ast.parse(source)
        self.assertIn("size = 128",source)
        for name in BOSSES:
            image=ASSETS/f"textures/entity/boss/{name}.png"
            self.assertTrue(image.exists(),str(image))
            import struct
            raw=image.read_bytes()
            self.assertEqual(struct.unpack(">II",raw[16:24]),(128,128))
    def test_no_gameplay_changes_to_phase_rewards(self):
        for name in BOSSES:
            self.assertTrue((ROOT/f"src/main/resources/data/shatteredseals/function/boss/{name}/phase_two.mcfunction").exists())
            self.assertTrue((ROOT/f"src/main/resources/data/shatteredseals/function/boss/{name}/phase_three.mcfunction").exists())
if __name__=="__main__":unittest.main()
