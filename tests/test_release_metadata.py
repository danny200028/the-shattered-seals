"""Release metadata and package feature coverage must stay consistent."""
from pathlib import Path
import unittest

ROOT=Path(__file__).resolve().parents[1]
VERSION="1.0.55"

class ReleaseMetadataTests(unittest.TestCase):
    def test_release_version_consistency(self):
        assert_contains={
            "build.gradle":f"version = '{VERSION}'",
            "src/main/resources/META-INF/neoforge.mods.toml":f'version="{VERSION}"',
            "src/main/java/com/shatteredseals/ShatteredSeals.java":f'VERSION = "{VERSION}"',
            "scripts/package_curseforge.py":f'VERSION = "{VERSION}"',
            ".github/workflows/build.yml":f"Shattered-Seals-{VERSION}-CurseForge-Modpack",
            "tests/verify_package.py":f'The-Shattered-Seals-{VERSION}-CurseForge.zip',
            "README.md":f"The-Shattered-Seals-{VERSION}-CurseForge.zip",
            "docs/PLAYTEST_CHECKLIST.md":VERSION
        }
        for name, needle in assert_contains.items():
            with self.subTest(file=name):
                self.assertIn(needle,(ROOT/name).read_text())

    def test_packager_requires_new_jar_features(self):
        packager=(ROOT/"scripts/package_curseforge.py").read_text()
        self.assertIn("QuestTrackerOverlay.class",packager)
        self.assertIn("ClassicSwordGuard.class",packager)
        self.assertIn("SwordGuard.class",packager)
        self.assertIn("RuntimeAudit.class",packager)
        self.assertIn("ShatteredBiomeRegion.class",packager)
        self.assertIn("WorldDiscovery.class",packager)
        self.assertIn("TEST_THE_SHATTERED_SEALS.cmd",packager)
        self.assertIn('loot_table/ritual_replay/{boss}.json',packager)

if __name__ == "__main__":
    unittest.main()
