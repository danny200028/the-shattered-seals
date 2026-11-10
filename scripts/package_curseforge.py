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
VERSION = "1.0.62"
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
    (238222, 8792638, "Just Enough Items (JEI 19.51.0.418, NeoForge 1.21.1)", "mezz"),
    (790626, 9309399, "ModernFix 5.27.27 (NeoForge 1.21.1)", "embeddedt"),
    (429235, 7524151, "FerriteCore 7.0.3 (NeoForge 1.21.1)", "malte0811"),
    (531761, 9406246, "Balm 21.0.67 (NeoForge 1.21.1; Waystones library)", "BlayTheNinth"),
    (245755, 8969751, "Waystones 21.1.46 (NeoForge 1.21.1)", "BlayTheNinth"),
    (940057, 6054947, "TerraBlender 4.1.0.8 (NeoForge 1.21.1, chapter biomes)", "Glitchfiend"),
)

README = """THE SHATTERED SEALS v1.0.62
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
6. JEI (recipe viewing), Waystones and its required Balm library (travel),
   ModernFix and FerriteCore (performance) are included as CurseForge
   dependency references. The pack deliberately does not add duplicate
   quest, dungeon, enchantment or skill-tree mods.
7. The J Codex Enchants page sells Common / Rare / Legendary books for
   160 / 550 / 1395 fixed XP points (equivalent to vanilla levels 10 / 20 / 30\n   from zero). Creative is free. 36 new enchantments each have levels I-III.
   Combine matching rank books in an anvil, use an anvil to enchant gear,
   or left-click the book onto compatible survival-inventory equipment.
8. All six legendary swords have a rare passive on successful melee hits,
   with independent cooldowns and stronger effects at higher sword tiers.
9. v1.0.61: Sword passive cooldowns display chat messages on activation
   and ready-again. Book hover tooltips track the actual enchantment rank
   after anvil combining. Survival inventory accepts enchant application
   in either click direction. Creative inventory requires an anvil.
10. TerraBlender adds six rare Overworld chapter biomes; each
   existing dungeon now generates only in its matching new biome.
   Six hidden lore shrines can appear nearby, with modest survival loot.
   J > Bestiary records six bosses. J > Explore tracks six biome visits
   and six lost shrines. New players see a short skippable RPG prologue.
   Replay the intro using /shatteredseals intro, without resetting progress.
   New biome generation ONLY appears in fresh/unexplored world chunks.
11. v1.0.61: J > Trials unlocks after level 60, a Hollow King victory and active NG+ chapter.
   Optional Hazard Pulse and Scarcity modifiers enhance the six boss fights;
   dual modifiers require NG+ cycle 3. One trial medal per chapter per cycle.
12. v1.0.61: J > Bounty offers hunts, biome surveys and chapter boss contracts.
   Only one contract may be active, claiming grants limited vanilla XP and
   ordinary resources, and rewards have a 10-minute cooldown.
13. J Codex includes optional rune hints and sword/relic ability descriptions;
   K Skill Tree now has detailed tooltips for skills and mastery perks.

AUTOMATED TESTING:
- Inside a new test world, type /shatteredseals audit in chat (no cheats required).
- Run /shatteredseals audit in your test world for built-in diagnostics.
- The optional Windows report collector is kept in the public source repository,
  but is NOT included in this gameplay ZIP to avoid shipping executable scripts.
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
            "com/shatteredseals/combat/LegendarySwordPassives.class",
            "com/shatteredseals/enchantments/CodexBookApplication.class",
            "com/shatteredseals/client/CodexBookTooltips.class",
            "com/shatteredseals/world/ShatteredBiomeRegion.class",
            "com/shatteredseals/world/WorldDiscovery.class",
            "com/shatteredseals/adventure/AdventureContracts.class",
            "com/shatteredseals/diagnostics/TrialsBountiesGameTests.class",
            "com/shatteredseals/diagnostics/FinalWorldGameTests.class",
            "com/shatteredseals/adventure/FinalAdventure.class",
            "com/shatteredseals/combat/SwordMastery.class",
            "com/shatteredseals/enchantments/UpgradePreservation.class",
            "com/shatteredseals/client/AnimatedBossModel.class",
            "com/shatteredseals/client/BossSkinRenderers.class",
            "com/shatteredseals/adventure/ChronicleRewards.class",
        )
        for required in required_classes:
            if required not in names:
                raise SystemExit(f"The first-party JAR is missing required feature: {required}")
        if "assets/shatteredseals/textures/models/armor/archivists_crown_layer_1.png" not in names:
            raise SystemExit("Missing wearable Archivist Crown armor model texture")
        for boss in ("smuggler_king", "cinder_warden", "hollow_knight",
                     "arcane_seer", "sixfold_guardian", "hollow_king"):
            sigil = f"data/shatteredseals/loot_table/ritual_replay/{boss}.json"
            if sigil not in names:
                raise SystemExit(f"The first-party JAR is missing NG+ sigil table: {boss}")
            skin = f"assets/shatteredseals/textures/entity/boss/{boss}.png"
            if skin not in names:
                raise SystemExit(f"Custom animated boss skin missing: {boss}")
        for name in ("smugglers_den", "cinder_citadel", "hollow_crypt",
                     "arcane_observatory", "sixfold_temple", "hollow_kings_fortress"):
            if f"data/shatteredseals/structure/{name}.nbt" not in names:
                raise SystemExit(f"First-party JAR missing generated dungeon: {name}")
        for new_structure in ("wardens_sanctuary", "smugglers_tunnels_ruin", "smugglers_tunnels_vault"):
            if f"data/shatteredseals/structure/{new_structure}.nbt" not in names:
                raise SystemExit(f"First-party JAR missing generated final content: {new_structure}")
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
        # Security-conscious distribution: no .cmd/.ps1/.bat/.exe helpers.
        # The optional Windows report collector stays in GitHub source only.
        # CurseForge itself never needs a launcher script to import the mod.
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
