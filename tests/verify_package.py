"""Validate CurseForge distribution after the real JAR is built."""
import hashlib
import json
from pathlib import Path
from zipfile import ZipFile
import unittest

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"build/distributions/The-Shattered-Seals-1.0.61-CurseForge.zip"
REQUIRED={
    (263420,9067173), (317780,9067277), (455508,6661598),
    (394468,6382651), (627557,8884654),
    (238222,8792638), (790626,9309399), (429235,7524151),
    (531761,9406246), (245755,8969751), (940057,6054947),
}
class PackageTests(unittest.TestCase):
    def test_import_archive(self):
        self.assertTrue(OUT.exists(),"Package ZIP not created")
        with ZipFile(OUT) as archive:
            self.assertIsNone(archive.testzip(),"ZIP CRC error")
            names=archive.namelist()
            self.assertIn("manifest.json",names)
            self.assertIn("overrides/README_THE_SHATTERED_SEALS.txt",names)
            # The public gameplay download must never bundle Windows scripts.
            unsafe_extensions = {".cmd", ".ps1", ".bat", ".exe", ".vbs", ".msi"}
            self.assertFalse([name for name in names
                              if Path(name).suffix.lower() in unsafe_extensions],
                             "Executable or PowerShell script inside gameplay ZIP")
            self.assertFalse([name for name in names if name.startswith("overrides/tools/")],
                             "Optional Windows test collector must remain source-only")
            self.assertTrue(all(".." not in Path(name).parts and not name.startswith("/") for name in names))
            mods=[name for name in names if name.startswith("overrides/mods/") and name.endswith(".jar")]
            self.assertEqual(len(mods),1)
            manifest=json.loads(archive.read("manifest.json"))
            self.assertEqual(manifest["minecraft"]["version"],"1.21.1")
            self.assertEqual(manifest["minecraft"]["modLoaders"],[{"id":"neoforge-21.1.238","primary":True}])
            self.assertEqual(manifest["version"],"1.0.61")
            self.assertEqual(manifest["overrides"],"overrides")
            self.assertEqual({(x["projectID"],x["fileID"]) for x in manifest["files"]},REQUIRED)
            self.assertTrue(all(x["required"] for x in manifest["files"]))
            self.assertEqual(len(mods)+len(manifest["files"]),12)
            # The credits must describe exactly the dependencies that CurseForge imports.
            credits=json.loads(archive.read("overrides/third_party_credits.json"))
            self.assertEqual(len(credits),len(manifest["files"]))
            self.assertEqual(
                [(x["projectID"],x["fileID"]) for x in credits],
                [(x["projectID"],x["fileID"]) for x in manifest["files"]],
                "Credits and CurseForge manifest disagree or contain duplicates")
            self.assertEqual(len({x["projectID"] for x in credits}),len(credits),
                             "Duplicate CurseForge project ID")
            self.assertTrue(all(isinstance(x["name"],str) and x["name"].strip()
                                and isinstance(x["author"],str) and x["author"].strip()
                                for x in credits), "Missing third-party attribution")
            with archive.open(mods[0]) as jar_stream:
                # JAR is already verified by the packager before inclusion.
                jar=jar_stream.read()
            recorded=archive.read("overrides/mod_sha256.txt").decode()
            self.assertEqual(
                recorded, f"{hashlib.sha256(jar).hexdigest()}  {Path(mods[0]).name}\\n",
                "Embedded mod checksum or filename is incorrect")
            self.assertGreater(len(jar),100000)
        check=(OUT.parent/(OUT.name+".sha256")).read_text()
        self.assertEqual(
            check, f"{hashlib.sha256(OUT.read_bytes()).hexdigest()}  {OUT.name}\\n",
            "Archive checksum or filename is incorrect")
