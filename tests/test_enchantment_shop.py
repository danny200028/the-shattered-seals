"""36 real 1.21.1 registry enchantments, books and XP purchase contracts."""
from pathlib import Path
import json, unittest
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
class EnchantmentTests(unittest.TestCase):
    def test_36_registry_enchantments(self):
        enchant=list((DATA/"enchantment").glob("*.json"))
        self.assertGreaterEqual(len(enchant),36)
        self.assertEqual(len(list((DATA/'function/enchantments').glob('give_*.mcfunction'))),36)
        for give in (DATA/'function/enchantments').glob('give_*.mcfunction'):
            p=DATA/'enchantment'/(give.stem.removeprefix('give_')+'.json')
            data=json.loads(p.read_text())
            self.assertEqual(data["max_level"],3,p.name)
            # These enchants resolve combined equipment effects on the server.
            java_driven = {"guardian_angel", "phoenix_skin", "lifebloom",
                           "immortal_spirit", "vampiric_edge", "soul_reaper",
                           "dragons_fury", "frostbite", "infernal_wrath",
                           "celestial_judgement", "thunderstrike", "soul_collector"}
            self.assertTrue(data["effects"] or p.stem in java_driven,p.name)
            self.assertTrue(data["supported_items"],p.name)
            self.assertEqual(data["description"]["italic"],False)
            self.assertTrue((DATA/"function/enchantments"/("give_"+p.stem+".mcfunction")).exists())
    def test_books_and_prices(self):
        for tier,level,price in (("common",10,160),("rare",20,550),("legendary",30,1395)):
            text=(DATA/"function/enchantments"/("buy_"+tier+".mcfunction")).read_text()
            self.assertIn("level="+str(level)+"..",text)
            self.assertIn("unless entity @s[gamemode=creative] unless entity @s[level="+str(level)+"..]",text)
            self.assertIn("execute unless entity @s[gamemode=creative] run xp add @s -"+str(price)+" points",text)
            self.assertIn("Need "+str(price)+" XP points (equivalent to level "+str(level)+")",text)
            self.assertNotIn("xp add @s -"+str(level)+" levels",text)
            self.assertLess(text.index("run return run tellraw"),text.index("ss_ebook_roll run random"))
            self.assertLess(text.index("ss_ebook_roll run random"),text.index("xp add @s -"+str(price)+" points"))
            self.assertEqual(text.count("function shatteredseals:enchantments/give_"),12)
        gui=(ROOT/"src/main/java/com/shatteredseals/client/AdventureScreen.java").read_text()
        self.assertIn("TIER_PRICES = {160, 550, 1395}",gui)
        self.assertIn('XP points)', gui)
        self.assertNotIn('XP levels)', gui)
        self.assertIn("scoreboard players enable @a ss_ebook",(DATA/"function/tick.mcfunction").read_text())
        self.assertIn("scoreboard objectives add ss_ebook trigger",(DATA/"function/load.mcfunction").read_text())
if __name__=="__main__":unittest.main()
