"""v1.0.56 read-only contracts: server authority, lifecycle and release integration."""
from pathlib import Path
import re
import unittest

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/com/shatteredseals"
CONTRACT=(JAVA/"adventure/AdventureContracts.java").read_text()
LOCATOR=(JAVA/"adventure/DungeonLocator.java").read_text()
NETWORK=(JAVA/"net/SkillSnapshot.java").read_text()
GUI=(JAVA/"client/AdventureScreen.java").read_text()
TESTS=(JAVA/"diagnostics/TrialsBountiesGameTests.java").read_text()

class ShatteredTrialsTests(unittest.TestCase):
    def test_story_ngplus_and_modifiers_have_server_side_checks(self):
        for required in ("trialUnlocked(", "ss_level", "ss_vk_king", "ss_ng", "ss_ngchapter",
                         "validTrialMask(", "ng >= 3", "trialWins", "trialCycle", "SSTrialOwner",
                         "SSTrialChapter", "SSTrialMask"):
            self.assertIn(required, CONTRACT)
        self.assertIn('boss.getTags().contains("ss_watch_linked")',CONTRACT)
        self.assertIn('mob.getTags().contains("ss_trial")',CONTRACT)
        self.assertIn("player.getUUID()",CONTRACT)
    def test_warning_pulse_healing_and_first_clear_protection(self):
        for token in ("SSTrialNextPulse", "SSTrialWarned", "LivingHealEvent",
                      "event.setAmount(event.getAmount() * 0.5F)", "WARNINGS[c - 1]",
                      "trialWins", "Shattered Trial Medal", "Math.min(180,"):
            self.assertIn(token,CONTRACT)
        self.assertNotIn("time % 100L",CONTRACT)

class BountyTests(unittest.TestCase):
    def test_at_most_one_active_bounty_and_no_unlimited_reward(self):
        for token in ("bountyKind", "bountyProgress", "bountyLastClaim",
                      "BOUNTY_COOLDOWN = 12000L", "bountyLastKill",
                      "bountyClaims", "if (state.getInt(\"bountyKind\") != 0)",
                      "state.putInt(\"bountyKind\", 0)", "required(kind, c)",
                      "claimBounty(", "abandonBounty(", "acceptBounty("):
            self.assertIn(token,CONTRACT)
        self.assertNotIn("Items.NETHERITE",CONTRACT)
        self.assertNotIn("Items.NETHER_STAR",CONTRACT)
        self.assertNotIn("Items.ENCHANTED_BOOK",CONTRACT)
    def test_bounty_kinds_have_actual_server_events(self):
        self.assertIn("LivingDeathEvent",CONTRACT)
        self.assertIn("PlayerTickEvent.Post",CONTRACT)
        self.assertIn("kind == 1",CONTRACT)
        self.assertIn("kind == 3",CONTRACT)
        self.assertIn('state.getInt("bountyKind") == 2',CONTRACT)
        self.assertIn("PlayerEvent.Clone",CONTRACT)
        self.assertIn("SSContractsV56",CONTRACT)
    def test_native_menu_and_server_packet_both_expose_contracts(self):
        for token in ('Commands.literal("trial")','Commands.literal("bounty")',
                      'Commands.literal("claim")','Commands.literal("abandon")'):
            self.assertIn(token,LOCATOR)
        for token in ("int[] trials, int[] bounties","AdventureContracts.trialState(player)",
                      "AdventureContracts.bountyState(player)","value.trials()[i]", "value.bounties()[i]"):
            self.assertIn(token,NETWORK)
        for token in ("Page.TRIALS", "Page.BOUNTIES", "renderTrials(", "renderBounties(",
                      'sendAdventure("trial arm "', 'sendAdventure("bounty accept "'):
            self.assertIn(token,GUI)
    def test_real_gametests_and_explicit_player_acceptance(self):
        self.assertEqual(len(re.findall(r'@GameTest\(',TESTS)),4)
        self.assertIn("validTrialMask(3, 3)",TESTS)
        self.assertIn("required(2, chapter)",TESTS)

if __name__=="__main__":
    unittest.main()
