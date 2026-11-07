# The Shattered Seals ⚔️

Minecraft Java **1.21.1** | **NeoForge 21.1.238** | **Java 21**

The world defaults to Hard difficulty, with high attack speed for an approximation of classic 1.8.9 full-strength combat. Holding right-click with a sword now blocks 40% of frontal, positioned incoming damage while the guard is active. This is not a byte-for-byte recreation of the old combat engine.

A challenging single-player RPG modpack, featuring a six-chapter boss storyline, nine-room natural jigsaw fortresses, class skill trees, powerful relics, rarity-based loot, and New Game+ mastery and repeatable chapter quests.

## Install the development pack

1. Open [GitHub Actions](https://github.com/danny200028/the-shattered-seals/actions/workflows/build.yml) and choose the latest successful **Build The Shattered Seals** run.
2. Download the artifact named **Shattered-Seals-1.0.47-CurseForge-Modpack**.
3. Extract that artifact ZIP **once**, exposing `The-Shattered-Seals-1.0.47-CurseForge.zip`.
4. Import the inner ZIP through the CurseForge Minecraft modpack importer. CurseForge resolves the pinned NeoForge map and shader dependencies; the custom Shattered Seals JAR lives in `overrides/mods`.
5. Start Minecraft 1.21.1 with Java 21. Use **J** for the Adventurer's Codex, **K** for the skill tree and **L** to toggle the on-screen quest tracker.

The shaderpack is **referenced** as a CurseForge dependency rather than redistributing its proprietary files. If CurseForge does not install it into `shaderpacks`, manually install [Complementary Reimagined](https://www.curseforge.com/minecraft/shaders/complementary-reimagined) and select it in the Iris shader menu. The mod's **own RPG textures** are in the compiled JAR.

### Controls and progression

- **J** Adventurer's Codex: six-chapter journal, next dungeon locator, relic arsenal, Relic Forge, and NG+ mastery.
- **K** Native four-class skill tree.
- **L** Toggle the on-screen chapter tracker with live rune-ward and keeper side-quest progress. The overlay reads server-owned scores and never grants progress.
- **Q / R / V / G** Four combat skill slots. **Important:** Minecraft also binds Drop to Q by default. Before combat, rebind either Drop or Ability 1 in Options > Controls > Key Binds so pressing Q does not drop your held item.
- **Hold right-click with a sword** for 1.8-style frontal sword guarding. The server checks your held weapon, incoming attack direction and a short-lived hold signal. Damage from behind and environmental damage are not blocked; shields still use vanilla mechanics.
- Complete the six dungeon chapters in order by defeating each boss. Summoning sigils spawn in the corresponding loot chest. Dungeon fortresses have six guardian spawners, magma and cobweb hazards, hidden caches and a ritual arena. Each of the six bosses now has distinct particle, audio and actionbar cues at both phase transitions. Dungeon locator uses Minecraft's `/locate structure` to find the current chapter.
- Character level cap is **60**. Clearing the Hollow King and reaching level 60 unlocks New Game+. Later NG+ cycles increase boss Strength and Resistance (capped from cycle four) while repeat boss mastery XP rises from 150 to 300. Each NG+ cycle resets the six rune wards and keeper challenges; completing a chapter's keeper quest awards one new ritual sigil so previously looted fortresses remain replayable.
- Dragon Egg skill resets require explicit confirmation and consume exactly one egg only on approval. Cancellation or closing the screen clears the server approval, and unused confirmations expire after 60 seconds.
- Summoned boss minions are encounter-owned and automatically removed after their associated boss is defeated or disappears, including cleanup of legacy minions in upgraded saves.

### Release testing

See [the acceptance checklist](docs/PLAYTEST_CHECKLIST.md) before labeling this version stable. It separates automated GitHub build success from actual Minecraft and CurseForge import testing.

### Quality gates and limitations

GitHub Actions automatically generates dungeon structures, runs structural/gameplay regression tests, compiles the Java mod and assembles the release ZIP. **A successful build does not substitute for an in-game test.** Please test fresh-world generation, dungeon placement, monster spawners, server commands, boss encounters, visual UI, shader loading, CurseForge import and save/reload before treating this as a stable release.

Third-party CurseForge projects are referenced by IDs. No proprietary mod/shader binaries are copied into this repository.
