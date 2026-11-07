"""Validate CurseForge distribution after the real JAR is built."""
import hashlib
import json
from pathlib import Path
from zipfile import ZipFile
import unittest

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"build/distributions/The-Shattered-Seals-1.0.47-CurseForge.zip"
REQUIRED={
    (263420,9067173), (317780,9067277), (455508,6661598),
    (394468,6382651), (627557,8884654),
}
class PackageTests(unittest.TestCase):
    def test_import_archive(self):
        self.assertTrue(OUT.exists(),"Package ZIP not created")
        with ZipFile(OUT) as archive:
            self.assertIsNone(archive.testzip(),"ZIP CRC error")
            names=archive.namelist()
            self.assertIn("manifest.json",names)
            self.assertIn("overrides/README_THE_SHATTERED_SEALS.txt",names)
            self.assertIn("overrides/TEST_THE_SHATTERED_SEALS.cmd",names)
            self.assertIn("overrides/tools/ShatteredSeals-TestReport.ps1",names)
            self.assertIn("shatteredseals audit",archive.read("overrides/tools/ShatteredSeals-TestReport.ps1").decode("utf-8"))
            self.assertTrue(all(".." not in Path(name).parts and not name.startswith("/") for name in names))
            mods=[name for name in names if name.startswith("overrides/mods/") and name.endswith(".jar")]
            self.assertEqual(len(mods),1)
            manifest=json.loads(archive.read("manifest.json"))
            self.assertEqual(manifest["minecraft"]["version"],"1.21.1")
            self.assertEqual(manifest["minecraft"]["modLoaders"],[{"id":"neoforge-21.1.238","primary":True}])
            self.assertEqual(manifest["version"],"1.0.47")
            self.assertEqual(manifest["overrides"],"overrides")
            self.assertEqual({(x["projectID"],x["fileID"]) for x in manifest["files"]},REQUIRED)
            self.assertTrue(all(x["required"] for x in manifest["files"]))
            self.assertEqual(len(mods)+len(manifest["files"]),6)
            with archive.open(mods[0]) as jar_stream:
                # JAR is already verified by the packager before inclusion.
                jar=jar_stream.read()
            recorded=archive.read("overrides/mod_sha256.txt").decode()
            self.assertIn(hashlib.sha256(jar).hexdigest(),recorded)
            self.assertGreater(len(jar),100000)
        check=(OUT.parent/(OUT.name+".sha256")).read_text()
        self.assertIn(hashlib.sha256(OUT.read_bytes()).hexdigest(),check)
