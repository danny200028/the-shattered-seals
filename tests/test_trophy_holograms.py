"""Trophy Hall labels are server-owned, earned-only, persistent and idempotent."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = (ROOT / "src/main/java/com/shatteredseals/adventure/FinalAdventure.java").read_text()
DATA = (ROOT / "src/main/java/com/shatteredseals/adventure/DungeonChapter.java").read_text()


class TrophyHologramTests(unittest.TestCase):
    def test_six_boss_origin_nameplates(self):
        for boss, dungeon in (
            ("Smuggler King", "Smugglers' Den"),
            ("Cinder Warden", "Cinder Citadel"),
            ("Hollow Knight", "Hollow Crypt"),
            ("Arcane Seer", "Arcane Observatory"),
            ("Sixfold Guardian", "Sixfold Temple"),
            ("Hollow King", "Hollow King's Fortress"),
        ):
            with self.subTest(boss=boss):
                self.assertIn(boss, DATA)
                self.assertIn(dungeon, DATA)
        for code in (
            "EntityType.TEXT_DISPLAY.create(level)",
            "DungeonChapter chapter = DungeonChapter.values()[index]",
            "chapter.boss", "chapter.dungeon",
            'displayNbt.putString("billboard", "center")',
            'displayNbt.putString("alignment", "center")',
            'displayNbt.putBoolean("shadow", true)',
            'displayNbt.putBoolean("see_through", false)',
        ):
            self.assertIn(code, JAVA)

    def test_only_earned_trophies_get_nameplates_and_existing_halls_upgrade(self):
        update = JAVA.split("private static void updateTrophies(ServerPlayer player) {", 1)[1]
        update = update.split("private static void ensureTrophyLabel(", 1)[0]
        self.assertIn("DungeonChapter.score(player, DungeonChapter.values()[i].victoryScore) > 0", update)
        self.assertIn("Display.ItemDisplay.class", update)
        self.assertIn("ensureTrophyLabel(level, cap, i);", update)
        self.assertIn("if (data.getBoolean(\"SSSanctuaryVisited\")) updateTrophies(player);", JAVA)
        self.assertIn('String labelTag = "ss_sanctuary_boss_label_" + index;', JAVA)

    def test_no_duplicate_labels_and_no_player_block_changes(self):
        label = JAVA.split("private static void ensureTrophyLabel(", 1)[1]
        label = label.split("@SubscribeEvent", 1)[0]
        self.assertIn("Display.TextDisplay.class", label)
        self.assertIn("display.getTags().contains(labelTag)", label)
        self.assertIn("label.saveWithoutId(new CompoundTag())", label)
        self.assertIn("label.load(displayNbt);", label)
        self.assertIn("label.addTag(labelTag);", label)
        self.assertIn("level.addFreshEntity(label);", label)
        self.assertNotIn("level.setBlock(", label)
        self.assertNotIn("setSlot(", label)
        self.assertNotIn("give(player,", label)


if __name__ == "__main__":
    unittest.main()
