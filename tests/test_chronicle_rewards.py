"""Readable Chronicle volume, Crown armour, progression and legacy compatibility guards."""
from pathlib import Path
import ast, unittest

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/com/shatteredseals"
ASSETS=ROOT/"src/main/resources/assets/shatteredseals"
BOSSES=("Smugglers' Tunnels","Emberfault Chasms","Drowned Ossuary",
         "Starshard Caverns","Rootbound Grottos","Black Crown Depths")

class ChronicleRewardTests(unittest.TestCase):
    def test_six_signed_readable_books_and_epilogue(self):
        text=(JAVA/"adventure/ChronicleRewards.java").read_text()
        self.assertIn("Items.WRITTEN_BOOK",text)
        self.assertIn("DataComponents.WRITTEN_BOOK_CONTENT",text)
        self.assertIn("new WrittenBookContent(",text)
        for name in BOSSES:self.assertIn(name,text)
        self.assertIn("epilogueBook()",text)
        self.assertIn("SSChronicleEpilogueGranted",text)

    def test_crown_is_wearable_and_has_unique_material(self):
        text=(JAVA/"ShatteredSeals.java").read_text()
        for phrase in ("DeferredRegister<ArmorMaterial>", "new ArmorMaterial(",
                       "ArmorItem.Type.HELMET, 4", "new ArmorMaterial.Layer(",
                       "3.0F, 0.1F", 'ITEMS.register("archivists_crown"',
                       "ARMOR_MATERIALS.register(modBus)"):
            self.assertIn(phrase,text)
        generator=(ROOT/"scripts/generate_v158_assets.py").read_text()
        ast.parse(generator)
        self.assertIn("crown_armor_texture()",generator)
        import struct
        texture=ASSETS/"textures/models/armor/archivists_crown_layer_1.png"
        self.assertTrue(texture.exists(),str(texture))
        self.assertEqual(struct.unpack(">II",texture.read_bytes()[16:24]),(64,32))

    def test_no_bonus_xp_farming_and_legacy_books(self):
        source=(JAVA/"adventure/FinalAdventure.java").read_text()
        reward=(JAVA/"adventure/ChronicleRewards.java").read_text()
        self.assertIn('if (playerData.getBoolean(clear)) return;',source)
        self.assertIn("ChronicleRewards.eliteXp(player)",source)
        self.assertIn("ChronicleRewards.chronicleBook(index)",source)
        self.assertIn("ChronicleRewards.ensureEpilogue(player)",source)
        for phrase in ('held.is(Items.PAPER)', 'held.shrink(1)',
                       'name.getString().equals("Buried Chronicle: "',
                       "getPersistentData().getInt(\"SSDepthsEliteMask\")",
                       "new ItemStack(Items.WRITTEN_BOOK)"):
            self.assertIn(phrase,reward)
        self.assertIn('ChronicleRewards.EPILOGUE_GRANT_KEY',source)

    def test_vault_sight_is_equipment_gated_and_throttled(self):
        text=(JAVA/"adventure/ChronicleRewards.java").read_text()
        for phrase in ("wearingCrown(player)", "SENSE_COOLDOWN_TICKS = 200",
                       "SENSE_RANGE = 48", "SSDepthsVaultsMask",
                       "SSArchivistNextPulse", "ss_depths_vault",
                       "player.displayClientMessage", "ParticleTypes.ENCHANT"):
            self.assertIn(phrase,text)
if __name__ == "__main__":unittest.main()
