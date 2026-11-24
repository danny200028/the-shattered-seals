"""Wither Skull Hunt regression: guaranteed at 20 skull-free player kills."""
from pathlib import Path
import unittest
ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/shatteredseals"
RULES = (JAVA / "progression/WitherSkullPity.java").read_text()
GAME = (JAVA / "diagnostics/WitherSkullPityGameTests.java").read_text()

class WitherSkullPityTests(unittest.TestCase):
    def test_player_credited_drops_and_looting_untouched(self):
        for value in ("LivingDropsEvent", "event.getSource().getEntity() instanceof ServerPlayer",
                      "WitherSkeleton", "Items.WITHER_SKELETON_SKULL",
                      "event.getDrops().stream()", "event.getDrops().add(skull)",
                      "new ItemStack(Items.WITHER_SKELETON_SKULL)",
                      "MAX_DRY_KILLS = 20", "onClone(PlayerEvent.Clone event)",
                      "getPersistentData().putInt(", "next(saved.getInt(COUNTER_KEY), vanillaSkull)"):
            self.assertIn(value, RULES)
        self.assertNotIn("event.getDrops().clear()", RULES)

    def test_counter_transitions_and_test_runtime(self):
        for value in ("if (vanillaSkullDropped) return new Result(0, false)",
                      "count >= MAX_DRY_KILLS", "new Result(0, true)",
                      "new Result(count, false)"):
            self.assertIn(value, RULES)
        for value in ("@GameTestHolder(ShatteredSeals.MOD_ID)",
                      "twentiethDryPlayerKillGuaranteesExactlyOneSkull",
                      "vanillaSkullResetsStreakWithoutDuplicate",
                      "environmentalAndOtherMobDeathsDoNotCount",
                      "player.damageSources().playerAttack(player)",
                      "helper.succeed()"):
            self.assertIn(value, GAME)
        self.assertIn("MAX_DRY_KILLS ? 1 : 0", GAME)
        self.assertIn("for (int i = 1; i <= 19; i++)", GAME)

if __name__ == "__main__":
    unittest.main()
