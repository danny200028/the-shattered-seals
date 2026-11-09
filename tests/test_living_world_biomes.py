"""v1.0.55 biome generation, difficulty and limited-encounter contracts."""
from pathlib import Path
import json, unittest
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/'src/main/resources/data/shatteredseals'
JAVA=(ROOT/'src/main/java/com/shatteredseals/world/ShatteredBiomeRegion.java').read_text()
GENERATOR=(ROOT/'scripts/generate_dungeons.py').read_text()
PACK=(ROOT/'scripts/package_curseforge.py').read_text()
BIOMES={
 'smugglers_wilds':'smugglers_den',
 'cinder_wastes':'cinder_citadel',
 'hollow_moor':'hollow_crypt',
 'arcane_highlands':'arcane_observatory',
 'sixfold_expanse':'sixfold_temple',
 'hollow_kings_dominion':'hollow_kings_fortress',
}
class LivingWorldBiomes(unittest.TestCase):
    def test_all_six_biomes_are_in_overworld_climate_region(self):
        self.assertEqual(JAVA.count('builder.replaceBiome('),6)
        self.assertIn('RegionType.OVERWORLD, 1',JAVA)
        self.assertIn('addModifiedVanillaOverworldBiomes',JAVA)
        for biome in BIOMES:
            self.assertIn('key("'+biome+'")',JAVA)
        self.assertIn('940057, 6054947',PACK)
        self.assertIn('terrablender', (ROOT/'src/main/resources/META-INF/neoforge.mods.toml').read_text())
    def test_biome_is_playable_and_structures_use_it(self):
        for biome,dungeon in BIOMES.items():
            with self.subTest(biome=biome):
                self.assertIn('"shatteredseals:'+biome+'"',GENERATOR)
                source=json.loads((DATA/'worldgen/biome'/f'{biome}.json').read_text())
                self.assertIn('minecraft:ore_dirt',source['features'][6])
                self.assertLess(source['features'][6].index('minecraft:ore_dirt'),
                                source['features'][6].index('minecraft:ore_coal_upper'))
                self.assertEqual(source['features'][9][-1],
                                 'shatteredseals:'+biome+'_surface_patch')
                self.assertIn('shatteredseals:'+biome+'_surface_patch',source['features'][9])
                self.assertTrue(source['spawners']['monster'])
                self.assertTrue((DATA/'worldgen/configured_feature'/f'{biome}_surface_patch.json').exists())
                generated=DATA/'worldgen/structure'/f'{dungeon}.json'
                self.assertEqual(json.loads(generated.read_text())['biomes'],'shatteredseals:'+biome)
if __name__=='__main__':unittest.main()
