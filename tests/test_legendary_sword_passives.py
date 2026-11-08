"""Legendary passives are individual on-hit, server-authoritative and tier-gated."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
JAVA=(ROOT/"src/main/java/com/shatteredseals/combat/LegendarySwordPassives.java").read_text()
class LegendarySwordPassiveTests(unittest.TestCase):
    def test_six_unique_powers(self):
        for name in ('emberbound','kings_fang','wardens_oath','starfall','gravekeeper','kings_bane'):
            self.assertIn('new Power("'+name+'"',JAVA)
            self.assertIn('case "'+name+'" ->',JAVA)
        self.assertEqual(JAVA.count('new Power('),6)
    def test_server_controls_rarity_and_tier(self):
        for phrase in ('LivingDamageEvent.Post','getNewDamage() <= 0','getDirectEntity() != player','getGameTime()',
                       'getPersistentData().putLong(cooldown','getRandom().nextFloat()','case "dormant" -> 1',
                       'case "awakened" -> 2','case "ascended" -> 3'):
            self.assertIn(phrase,JAVA)
if __name__=="__main__":unittest.main()
