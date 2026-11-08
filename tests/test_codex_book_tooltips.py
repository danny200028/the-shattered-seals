"""Book tooltips display actual stored rank for all 36 custom enchantments."""
from pathlib import Path
import unittest, re
ROOT=Path(__file__).resolve().parents[1]
JAVA=(ROOT/"src/main/java/com/shatteredseals/client/CodexBookTooltips.java").read_text()
class BookTooltipTests(unittest.TestCase):
    def test_all_36_books_have_metadata(self):
        ids=re.findall(r'Map.entry\("([^"]+)"',JAVA)
        self.assertEqual(len(ids),36)
        self.assertEqual(len(set(ids)),36)
        for id in ids:
            self.assertTrue((ROOT/"src/main/resources/data/shatteredseals/function/enchantments"/("give_"+id+".mcfunction")).exists(),id)
    def test_display_is_dynamic_not_original_lore(self):
        for text in ('ItemTooltipEvent','DataComponents.STORED_ENCHANTMENTS',
                     'entry.getIntValue()', 'Level " + level + " / III',
                     'text.contains(" | Level ")','Maximum enchantment level reached.',
                     'ROMAN[rank + 1]'):
            self.assertIn(text,JAVA)
if __name__=="__main__":unittest.main()
