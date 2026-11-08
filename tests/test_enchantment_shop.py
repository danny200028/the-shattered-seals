"""36 real 1.21.1 registry enchantments, books and XP purchase contracts."""
from pathlib import Path
import json, unittest
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
class EnchantmentTests(unittest.TestCase):
    def test_36_registry_enchantments(self):
        enchant=list((DATA/"enchantment").glob("*.json"))
        self.assertEqual(len(enchant),36)
        for p in enchant:
            data=json.loads(p.read_text())
            self.assertEqual(data["max_level"],3,p.name)
            self.assertTrue(data["effects"],p.name)
            self.assertTrue(data["supported_items"],p.name)
            self.assertEqual(data["description"]["italic"],False)
            self.assertTrue((DATA/"function/enchantments"/("give_"+p.stem+".mcfunction")).exists())
    def test_books_and_prices(self):
        for tier,price in (("common",10),("rare",20),("legendary",30)):
            text=(DATA/"function/enchantments"/("buy_"+tier+".mcfunction")).read_text()
            self.assertIn("level="+str(price)+"..",text)
            self.assertIn("xp add @s -"+str(price)+" levels",text)
            self.assertEqual(text.count("function shatteredseals:enchantments/give_"),12)
        self.assertIn("scoreboard players enable @a ss_ebook",(DATA/"function/tick.mcfunction").read_text())
        self.assertIn("scoreboard objectives add ss_ebook trigger",(DATA/"function/load.mcfunction").read_text())
if __name__=="__main__":unittest.main()
