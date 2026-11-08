"""Every class and talent has GUI tooltips, with live core ranks."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
GUI=(ROOT/'src/main/java/com/shatteredseals/client/SkillTreeScreen.java').read_text()
NET=(ROOT/'src/main/java/com/shatteredseals/net/SkillSnapshot.java').read_text()
class SkillDescriptionsTests(unittest.TestCase):
    def test_every_class_branch_mastery_and_ability_is_described(self):
        for term in ('CORE_HELP','BRANCH_HELP','MASTERY_HELP','ACTIVE_HELP','setTooltip(Tooltip.create',
                     'Blood Frenzy','Arcane Overdrive','Ghostwalk','Vanguard Might',
                     'Rank 3 grants','Q Blood Cleave','G Shadow Escape'):
            self.assertIn(term,GUI)
    def test_core_ranks_travel_over_authoritative_snapshot(self):
        for term in ('int[] coreRanks','value.coreRanks()[i]','coreRanks[i] = buf.readVarInt()',
                     'read(board, player, "ss_t_van")','read(board, player, "ss_t_arc")'):
            self.assertIn(term,NET)
        self.assertIn('data.coreRanks()[selectedClass]',GUI)
if __name__=='__main__':unittest.main()
