# The Shattered Seals ⚔️

Minecraft Java **1.21.1** | **NeoForge 21.1.238** | **Java 21**

The world defaults to Hard difficulty, with high attack speed for an approximation of classic 1.8.9 full-strength combat. This is not a byte-for-byte recreation of the old combat engine.\n\nA challenging single-player RPG modpack, featuring a six-chapter boss storyline, nine-room natural jigsaw fortresses, class skill trees, powerful relics, rarity-based loot, and New Game+ mastery.

## Install the development pack

1. Open [GitHub Actions](https://github.com/danny200028/the-shattered-seals/actions/workflows/build.yml) and choose the latest successful **Build The Shattered Seals** run.
2. Download the artifact named **Shattered-Seals-1.0.46-CurseForge-Modpack**.
3. Extract that artifact ZIP **once**, exposing `The-Shattered-Seals-1.0.46-CurseForge.zip`.
4. Import the inner ZIP through the CurseForge Minecraft modpack importer. CurseForge resolves the pinned NeoForge map and shader dependencies; the custom Shattered Seals JAR lives in `overrides/mods`.
5. Start Minecraft 1.21.1 with Java 21. Use **J** for the Adventurer's Codex, **K** for the skill tree and **L** to toggle the on-screen quest tracker.

The shaderpack is **referenced** as a CurseForge dependency rather than redistributing its proprietary files. If CurseForge does not install it into `shaderpacks`, manually install [Complementary Reimagined](https://www.curseforge.com/minecraft/shaders/complementary-reimagined) and select it in the Iris shader menu. The mod's **own RPG textures** are in the compiled JAR.

### Controls and progression

- **J** Adventurer's Codex: six-chapter journal, next dungeon locator, relic arsenal, Relic Forge, and NG+ mastery.
- **K** Native four-class skill tree.
- **L** Toggle the on-screen chapter tracker with live rune-ward and keeper side-quest progress. The overlay reads server-owned scores and never grants progress.
- **Q / R / V / G** Four combat skill slots.
- Complete the six dungeon chapters in order by defeating each boss. Summoning sigils spawn in the corresponding loot chest. Dungeon fortresses have six guardian spawners, magma and cobweb hazards, hidden caches and a ritual arena. Dungeon locator uses Minecraft's `/locate structure` to find the current chapter.
- Character level cap is **60**. Clearing the Hollow King and reaching level 60 unlocks New Game+.
- Dragon Egg skill resets require explicit confirmation, consume one egg, refund normal and invested NG+ mastery allocations.

### Quality gates and limitations

GitHub Actions automatically generates dungeon structures, runs structural/gameplay regression tests, compiles the Java mod and assembles the release ZIP. **A successful build does not substitute for an in-game test.** Please test fresh-world generation, dungeon placement, monster spawners, server commands, boss encounters, visual UI, shader loading, CurseForge import and save/reload before treating this as a stable release.

Third-party CurseForge projects are referenced by IDs. No proprietary mod/shader binaries are copied into this repository.
