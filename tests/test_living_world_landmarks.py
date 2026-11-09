"""Six deterministic shrines have modest loot and real discovery markers."""
import importlib.util, json, gzip, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/'src/main/resources/data/shatteredseals'
SPEC=importlib.util.spec_from_file_location('dungeons', ROOT/'scripts/generate_dungeons.py')
GEN=importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(GEN)
class LandmarkTests(unittest.TestCase):
    def test_generated_shrines_and_markers(self):
        for chapter,(_,biome,*rest) in enumerate(GEN.THEMES,1):
            name=biome.removeprefix('shatteredseals:')+'_lost_shrine'
            with self.subTest(name=name):
                raw=gzip.decompress((DATA/f'structure/{name}.nbt').read_bytes())
                self.assertIn(('ss_landmark_'+biome.removeprefix('shatteredseals:')).encode(),raw)
                self.assertIn(b'shatteredseals:chests/forgotten_shrine',raw)
                self.assertIn(b'minecraft:marker',raw)
                structure=json.loads((DATA/f'worldgen/structure/{name}.json').read_text())
                self.assertEqual(structure['biomes'],biome)
                spawn=json.loads((DATA/f'worldgen/structure_set/{name}.json').read_text())
                self.assertGreaterEqual(spawn['placement']['spacing'],20)
                self.assertLess(spawn['placement']['separation'],spawn['placement']['spacing'])
        loot=json.loads((DATA/'loot_table/chests/forgotten_shrine.json').read_text())
        self.assertEqual(loot['pools'][0]['rolls'],2)
        self.assertNotIn('netherite',json.dumps(loot))
    def test_discovery_is_per_player_and_save_safe(self):
        java=(ROOT/'src/main/java/com/shatteredseals/world/WorldDiscovery.java').read_text()
        self.assertIn('SSBiomeDiscoveryMask',java)
        self.assertIn('SSLandmarkDiscoveryMask',java)
        self.assertIn('ss_landmark_',java)
        self.assertIn('player.getPersistentData()',java)
        self.assertIn('ss_path',java)
if __name__=='__main__':unittest.main()
