#!/usr/bin/env python3
"""Assemble a reproducible CurseForge import ZIP with the compiled first-party mod.

Third-party mods and the optional shader pack are *references* to specific
CurseForge projects/files. Never redistribute their binaries from this script.
"""
from __future__ import annotations

import hashlib
import json
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile

ROOT = Path(__file__).resolve().parents[1]
VERSION = "1.0.50"
MC_VERSION = "1.21.1"
LOADER = "neoforge-21.1.238"
OUT_DIR = ROOT / "build/distributions"
OUTPUT = OUT_DIR / f"The-Shattered-Seals-{VERSION}-CurseForge.zip"

# Compatible release IDs, validated against each project's published CurseForge page.
DEPENDENCIES = (
    # Project ID, file ID, label, author/publisher
    (263420, 9067173, "Xaero's Minimap (NeoForge 1.21.1)", "xaero96"),
    (317780, 9067277, "Xaero's World Map (NeoForge 1.21.1)", "xaero96"),
    (455508, 6661598, "Iris Shaders 1.8.12 (NeoForge 1.21.1)", "Iris Team"),
    (394468, 6382651, "Sodium 0.6.13 (NeoForge 1.21.1)", "CaffeineMC"),
    (627557, 8884654, "Complementary Reimagined r5.9.3 (shader pack)", "EminGT"),
)

README = """THE SHATTERED SEALS v1.0.50
Minecraft Java 1.21.1 / NeoForge 21.1.238 / Java 21

CURSEFORGE IMPORT:
1. In the CurseForge app, create/import a Minecraft modpack and select this ZIP.
2. The importer should install the listed NeoForge dependencies and place
   The Shattered Seals mod JAR from overrides/mods automatically.
3. The mod's own dark-fantasy textures, models and UI are bundled inside its JAR.
4. Enable shaders through Iris's shader selection menu (normally O). The
   Complementary Reimagined file is referenced through CurseForge. If your
   importer does not install it to shaderpacks automatically, add it via
   its CurseForge project page instead:
   https://www.curseforge.com/minecraft/shaders/complementary-reimagined
5. Xaero's Minimap and World Map should be installed by the importer; use the
   minimap and waypoint settings to mark discovered dungeons.

AUTOMATED TESTING:
- Inside a new test world, type /shatteredseals audit in chat (no cheats required).
- Quit Minecraft and double-click TEST_THE_SHATTERED_SEALS.cmd in your
  CurseForge instance folder (the same folder as mods and saves).
- It creates ShatteredSeals-TestReports with a summary and ZIP to share.
- The in-world audit checks live server registries, functions, loot,
  progression data and installed-mod presence WITHOUT changing progress.
- Visual shaders, actual boss combat, terrain placement and save/reload still
  need human verification. The report marks these NOT_TESTED.

FIRST PLAY:
- K opens the native 4-class skill tree.
- J opens the native Adventurer's Codex: journal, relics, forge and NG+ mastery.
- Q/R/V/G cast your four class ability slots.
- L toggles the chapter quest tracker (including keeper quests and rune wards).
- Hold right-click with a sword to guard against frontal attacks.
- The in-game journal includes Locate Next Dungeon. Boss sigils are guaranteed
  in the matching chapter dungeon loot chest.
- Defeat all six bosses in order. Hollow King unlocks NG+ after level 60.
- New Game+ renews sigils through chapter keepers and increases boss difficulty.
- A full skill-tree respec consumes exactly one Dragon Egg *after confirmation*.

WARNING: This is a development/acceptance build. GitHub validates source, generated
dungeon files, gameplay functions and Java compilation. It does NOT run Minecraft
in a client to verify world generation, shaders, visuals or encounters. A clean
in-game singleplayer test is still required.

Licensing: Third-party mods and shaders are installed via CurseForge file IDs.
No third-party JARs, shader ZIPs or texture-pack binaries are redistributed.
"""

def select_jar() -> Path:
    artifacts = sorted((ROOT / "build/libs").glob("the-shattered-seals-*.jar"))
    mods = [jar for jar in artifacts if not any(
        jar.name.endswith(suffix) for suffix in ("-sources.jar", "-javadoc.jar", "-dev.jar")
    )]
    if len(mods) != 1:
        raise SystemExit(f"Expected one built mod JAR, found {[p.name for p in mods]}")
    with ZipFile(mods[0]) as jar:
        names = set(jar.namelist())
        if "META-INF/neoforge.mods.toml" not in names:
            raise SystemExit("The first-party JAR lacks NeoForge mod metadata")
        if "com/shatteredseals/client/AdventureScreen.class" not in names:
            raise SystemExit("The first-party JAR is missing the Adventure Codex GUI")
        # Never ship a release that silently drops the latest integration systems.
        required_classes = (
            "com/shatteredseals/client/QuestTrackerOverlay.class",
            "com/shatteredseals/combat/ClassicSwordGuard.class",
            "com/shatteredseals/net/SwordGuard.class",
            "com/shatteredseals/diagnostics/RuntimeAudit.class",
        )
        for required in required_classes:
            if required not in names:
                raise SystemExit(f"The first-party JAR is missing required feature: {required}")
        for boss in ("smuggler_king", "cinder_warden", "hollow_knight",
                     "arcane_seer", "sixfold_guardian", "hollow_king"):
            sigil = f"data/shatteredseals/loot_table/ritual_replay/{boss}.json"
            if sigil not in names:
                raise SystemExit(f"The first-party JAR is missing NG+ sigil table: {boss}")
        for name in ("smugglers_den", "cinder_citadel", "hollow_crypt",
                     "arcane_observatory", "sixfold_temple", "hollow_kings_fortress"):
            if f"data/shatteredseals/structure/{name}.nbt" not in names:
                raise SystemExit(f"First-party JAR missing generated dungeon: {name}")
    return mods[0]

def main() -> None:
    mod = select_jar()
    manifest = {
        "minecraft": {
            "version": MC_VERSION,
            "modLoaders": [{"id": LOADER, "primary": True}]
        },
        "manifestType": "minecraftModpack",
        "manifestVersion": 1,
        "name": "The Shattered Seals",
        "version": VERSION,
        "author": "The Shattered Seals Project",
        "files": [
            {"projectID": project, "fileID": file, "required": True}
            for project, file, _label, _author in DEPENDENCIES
        ],
        "overrides": "overrides",
    }
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    mod_bytes = mod.read_bytes()
    with ZipFile(OUTPUT, "w", compression=ZIP_DEFLATED, compresslevel=9) as pack:
        pack.writestr("manifest.json", json.dumps(manifest, indent=2) + "\n")
        pack.writestr(f"overrides/mods/{mod.name}", mod_bytes)
        pack.writestr("overrides/README_THE_SHATTERED_SEALS.txt", README)
        # Double-clickable Windows report collector, installed into the profile root.
        for source, target in (
            ("tools/windows/TEST_THE_SHATTERED_SEALS.cmd", "overrides/TEST_THE_SHATTERED_SEALS.cmd"),
            ("tools/windows/ShatteredSeals-TestReport.ps1", "overrides/tools/ShatteredSeals-TestReport.ps1"),
        ):
            path = ROOT / source
            if not path.is_file():
                raise SystemExit(f"Missing Windows testing tool: {source}")
            pack.write(path, target)
        pack.writestr("overrides/third_party_credits.json",
                      json.dumps([{"projectID": project, "fileID": file,
                                   "name": label, "author": author}
                                  for project, file, label, author in DEPENDENCIES],
                                 indent=2) + "\n")
        pack.writestr("overrides/mod_sha256.txt",
                      f"{hashlib.sha256(mod_bytes).hexdigest()}  {mod.name}\n")
    checksum = hashlib.sha256(OUTPUT.read_bytes()).hexdigest()
    (OUT_DIR / (OUTPUT.name + ".sha256")).write_text(
        f"{checksum}  {OUTPUT.name}\n"
    )
    print(f"CurseForge import archive: {OUTPUT} ({OUTPUT.stat().st_size} bytes)")
    print(f"SHA256: {checksum}")
    print(f"Pinned CurseForge third-party entries: {len(DEPENDENCIES)}")

if __name__ == "__main__":
    main()
