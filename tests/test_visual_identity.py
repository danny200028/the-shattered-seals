"""Original Ashen Covenant art, modpack visuals and shader settings."""
import json
import struct
import unittest
from pathlib import Path
from zipfile import ZipFile

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"build/brand"
ASSETS=ROOT/"src/main/resources/assets/shatteredseals/textures/gui"

class VisualIdentityTests(unittest.TestCase):
    def test_gui_art_and_thumbnail(self):
        for path,dims in (
            (ASSETS/"title_banner.png",(256,80)),
            (ASSETS/"loading_banner.png",(512,256)),
            (OUT/"curseforge_thumbnail.png",(512,512))
        ):
            self.assertTrue(path.exists(),path)
            raw=path.read_bytes()
            self.assertEqual(raw[:8],b"\x89PNG\r\n\x1a\n")
            self.assertEqual(struct.unpack(">II",raw[16:24]),dims)

    def test_original_resource_pack(self):
        path=OUT/"The-Shattered-Seals-Ashen-Covenant.zip"
        self.assertTrue(path.exists())
        with ZipFile(path) as z:
            self.assertIsNone(z.testzip())
            meta=json.loads(z.read("pack.mcmeta"))
            self.assertEqual(meta["pack"]["pack_format"],34)
            self.assertIn("pack.png",z.namelist())
            self.assertGreaterEqual(len(
                [n for n in z.namelist() if n.endswith(".png")]),7)

    def test_rune_material_textures_match_the_actual_story_clues(self):
        # Each material is used by a real rune marker; colour must distinguish
        # copper (SUN), prismarine (TIDE), and blackstone (ASH), including
        # when oxidized copper trim appears in the temple.
        path=OUT/"The-Shattered-Seals-Ashen-Covenant.zip"
        keys=("chiseled_copper", "chiseled_copper_top",
              "oxidized_copper", "prismarine", "polished_blackstone")
        with ZipFile(path) as z:
            for key in keys:
                location=f"assets/minecraft/textures/block/{key}.png"
                self.assertIn(location,z.namelist())
                data=z.read(location)
                self.assertEqual(data[:8],b"\\x89PNG\\r\\n\\x1a\\n")
                self.assertEqual(struct.unpack(">II",data[16:24]),(16,16))
            def avg_rgb(name):
                import zlib
                data=z.read(f"assets/minecraft/textures/block/{name}.png")
                pos=data.index(b"IDAT")
                size=struct.unpack(">I",data[pos-4:pos])[0]
                raw=zlib.decompress(data[pos+4:pos+4+size])
                pixels=[tuple(raw[1+y*65+x*4:1+y*65+x*4+3])
                        for y in range(16) for x in range(16)]
                return tuple(sum(p[channel] for p in pixels)/len(pixels)
                             for channel in range(3))
            for name in ("chiseled_copper","chiseled_copper_top","oxidized_copper"):
                red,green,blue=avg_rgb(name)
                self.assertGreater(red,green+35,name)
                self.assertGreater(green,blue+18,name)
            red,green,blue=avg_rgb("prismarine")
            self.assertGreater(blue,red+60)
            self.assertGreater(blue,green+25)
            red,green,blue=avg_rgb("polished_blackstone")
            self.assertLess(max(red,green,blue),85)

    def test_title_and_loading_menus_keep_vanilla_navigation(self):
        source=(ROOT/"src/main/java/com/shatteredseals/client/BrandedScreens.java").read_text()
        self.assertIn("ScreenEvent.Render.Post",source)
        self.assertIn("instanceof TitleScreen",source)
        self.assertIn('name.equals("ReceivingLevelScreen")',source)
        self.assertIn('"textures/gui/loading_banner.png"',source)
        self.assertNotIn("setNewScreen(",source)
        self.assertNotIn("event.setCanceled(",source)

if __name__=="__main__":
    unittest.main()
