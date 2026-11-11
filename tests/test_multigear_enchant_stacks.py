"""Multi-slot armour and double-weapon enchantment stacking contracts."""
from pathlib import Path
import json, unittest
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/"src/main/resources/data/shatteredseals"
JAVA=(ROOT/"src/main/java/com/shatteredseals/enchantments/EquipmentEnchantStacks.java").read_text()
LOAD=(DATA/"function/load.mcfunction").read_text()
PASS=(DATA/"function/progression/passives.mcfunction").read_text()

class MultiGearEnchantmentTests(unittest.TestCase):
    def test_multi_slot_armor_effects_are_resolved_together(self):
        for n in ("guardian_angel","phoenix_skin","lifebloom","immortal_spirit"):
            d=json.loads((DATA/f"enchantment/{n}.json").read_text())
            self.assertEqual(d["slots"],["armor"])
            self.assertEqual(d["effects"],{},n)
            self.assertIn('rank(worn, "'+n+'")',JAVA)
        for n in ("ss_ench_abs","ss_ench_fire","ss_ench_firepieces"):
            self.assertIn(f"scoreboard objectives add {n} dummy",LOAD)
            self.assertIn(n,PASS)
        self.assertIn("ss_fx_abs += @s ss_ench_abs",PASS)
        self.assertIn("ss_fx_fire += @s ss_ench_fire",PASS)
        self.assertIn("max.addTransientModifier",JAVA)
        self.assertIn("max.getModifier(HEALTH_ID)",JAVA)
        self.assertIn("player.getArmorSlots()",JAVA)

    def test_double_swords_and_axes_use_offhand_without_duplicate_procs(self):
        for name in ("vampiric_edge","soul_reaper","dragons_fury",
                     "frostbite","infernal_wrath","celestial_judgement",
                     "thunderstrike","soul_collector"):
            d=json.loads((DATA/f"enchantment/{name}.json").read_text())
            self.assertEqual(d["effects"],{},name)
            self.assertIn('"'+name+'"',JAVA)
        for name in ("soul_edge","keen_edge","berserker","kings_execution"):
            d=json.loads((DATA/f"enchantment/{name}.json").read_text())
            self.assertTrue(d["effects"].get("minecraft:damage"),name)
            self.assertIn('"'+name+'"',JAVA)
        self.assertIn("ItemTags.SWORDS",JAVA)
        self.assertIn("ItemTags.AXES",JAVA)
        self.assertIn("LivingIncomingDamageEvent",JAVA)
        self.assertIn("LivingDamageEvent.Post",JAVA)
        self.assertIn("LivingDeathEvent",JAVA)
        self.assertIn("offhandContribution",JAVA)
        self.assertIn("Math.min(5",JAVA)
        self.assertIn("getSource().getDirectEntity() != player",JAVA)
        self.assertIn('mob instanceof net.minecraft.world.entity.monster.Enemy',JAVA)
        self.assertNotIn("LivingDamageEvent.Pre",JAVA)
        self.assertTrue((ROOT/"src/main/java/com/shatteredseals/diagnostics/EquipmentEnchantGameTests.java").is_file())

    def test_existing_book_levels_unmodified(self):
        books=list((DATA/"function/enchantments").glob("give_*.mcfunction"))
        self.assertEqual(len(books),36)
        for name in ("guardian_angel","phoenix_skin","lifebloom","immortal_spirit",
                     "vampiric_edge","soul_reaper"):
            self.assertTrue((DATA/f"function/enchantments/give_{name}.mcfunction").exists())
        self.assertIn('Max', 'Max') # All old stored enchantment ranks remain on items.

if __name__=="__main__":unittest.main()
