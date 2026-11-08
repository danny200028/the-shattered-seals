"""No feature duplicates or unchecked addon versions in the v1.0.53 package."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
PACK=(ROOT/"scripts/package_curseforge.py").read_text()
class CompatibilityTests(unittest.TestCase):
    def test_compatible_version_pins(self):
        for pair in ("238222, 8792638","790626, 9309399","429235, 7524151",
                     "531761, 9406246","245755, 8969751"):
            self.assertIn(pair,PACK)
        for duplicate in ("FTB Quests","Pufferfish Skills","Apotheosis","Sol's Item Rarity"):
            self.assertNotIn('"'+duplicate+'"',PACK)
if __name__=='__main__':unittest.main()
