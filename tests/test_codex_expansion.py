"""Codex pages expose rune solutions, all enchantments, and sword/relic powers."""
from pathlib import Path
import unittest
S=(Path(__file__).resolve().parents[1]/'src/main/java/com/shatteredseals/client/AdventureScreen.java').read_text()
class CodexExpansionTests(unittest.TestCase):
    def test_enchant_catalogue_and_tier_purchase(self):
        self.assertIn('case ENCHANTS -> renderEnchants',S)
        self.assertIn('perform("ss_ebook", enchantTier + 1)',S)
        for name in ('Keen Edge','Vampiric Edge','Soul Reaper'):
            self.assertIn(name,S)
        self.assertIn('Book I + Book I = II',S)
    def test_rune_hints_and_relics(self):
        self.assertIn('Rune order:',S)
        self.assertIn('chapter.runeAt(0)',S)
        self.assertIn('showRuneHint',S)
        self.assertIn('case RELICS -> renderRelics',S)
        self.assertIn('Warden\'s Pulse',S)
    def test_sword_passive_summary(self):
        self.assertIn('SWORD_POWERS',S)
        self.assertIn('5% | 50s',S)
        self.assertIn('Dormant to Awakened to Ascended',S)
if __name__=="__main__":unittest.main()
