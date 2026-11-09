"""Prevent regressions in Forge enchantment upgrade safety."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
FUNCTIONS=ROOT/"src/main/resources/data/shatteredseals/function/upgrade"
class SafeUpgradeTests(unittest.TestCase):
    def test_all_twelve_paths_remember_then_restore(self):
        files=sorted(FUNCTIONS.glob("*_apply.mcfunction"))
        self.assertEqual(len(files),12)
        for path in files:
            s=path.read_text()
            with self.subTest(path=path.name):
                old=s.index("shatteredseals upgrade remember")
                clear=s.index("item replace entity @s weapon.mainhand with minecraft:air")
                new=s.index("function shatteredseals:gear/")
                after=s.index("shatteredseals upgrade restore")
                self.assertLess(old,clear)
                self.assertLess(clear,new)
                self.assertLess(new,after)
    def test_no_item_dupe_and_builtin_enchant_preservation(self):
        p=ROOT/"src/main/java/com/shatteredseals/enchantments/UpgradePreservation.java"
        src=p.read_text()
        self.assertIn("Math.max(builtIn, original)",src)
        self.assertIn("EnchantmentHelper.updateEnchantments",src)
        self.assertNotIn("player.getInventory().add(",src)
        self.assertNotIn("giveExperiencePoints(",src)
        cmd=(ROOT/"src/main/java/com/shatteredseals/adventure/DungeonLocator.java").read_text()
        self.assertIn('Commands.literal("upgrade")',cmd)
        self.assertIn('Commands.literal("remember")',cmd)
        self.assertIn('Commands.literal("restore")',cmd)
        self.assertIn("hasPermission(2)",cmd)
if __name__ == "__main__":
    unittest.main()
