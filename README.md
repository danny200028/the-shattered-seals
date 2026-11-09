# The Shattered Seals ⚔️

Minecraft Java **1.21.1** | **NeoForge 21.1.238** | **Java 21**

The world defaults to Hard difficulty, with high attack speed for an approximation of classic 1.8.9 full-strength combat. Holding right-click with a sword now blocks 40% of frontal, positioned incoming damage while the guard is active. This is not a byte-for-byte recreation of the old combat engine.

A challenging single-player RPG modpack, featuring a six-chapter boss storyline, nine-room natural jigsaw fortresses, class skill trees, powerful relics, rarity-based loot, and New Game+ mastery and repeatable chapter quests.

## Locked final development roadmap (originally 8 November 2026; expanded 9 November)

**Feature scope targets v1.1.0**, with one explicitly user-approved addition:
**Shattered Depths**, a complete optional underground exploration system.
See the [canonical roadmap](docs/ROADMAP.md) for v1.0.55 Living World and six
surface biomes, v1.0.56 Shattered Trials/Bounty Board, v1.0.57 The Warden's
Sanctuary and legendary equipment mastery, v1.0.58 Shattered Depths, secret
endgame encounter and player settings, v1.0.59 final balance/QA, and v1.1.0
complete CurseForge release.

**Shattered Depths was not included in v1.0.56, but is now implemented in the combined v1.0.57 acceptance build.** It introduces
six thematic underground regions tied to the six chapters; cave ruins,
catacombs and rare treasure vaults; optional dangerous encounters and modest
treasure; and new lore/discovery tracking in the Codex. Vanilla caves, mining,
ores, previous worlds and the original story progression must remain intact.
No new power tiers or mandatory underground progression gates are authorised.
Additional major expansions still require explicit user approval.

### v1.0.56: Shattered Trials and Adventurer's Bounty Board (13/13 manual tests passed)

- J Codex now offers **Trials** and **Bounty** tabs. Shattered Trials unlock after
  character level 60, first Hollow King victory and an active New Game+ chapter.
  Choose a telegraphed **Hazard Pulse** (1), arena **Scarcity** (healing reduced, 2),
  or both (3) from NG+ cycle 3 onward. Summon that chapter's boss at its existing
  arena. Each chapter can award just one trial medal per NG+ cycle, plus bounded
  amethyst and vanilla XP. The normal campaign and regular boss battles remain
  available without these modifiers.
- The **Bounty Board** gives one active contract: chapter-specific monster hunts,
  surveys of themed surface biomes, or defeating the current chapter boss.
  Progress and explicit claim/cancel choices are saved server-side, and rewards
  are intentionally modest. A 10-minute cooldown after claiming limits farming.
- Bounty commands (no cheats needed): `/shatteredseals bounty accept 1` (hunt),
  `accept 2` (survey), `accept 3` (guardian), `bounty claim` and
  `bounty abandon`. Trial commands: `/shatteredseals trial arm 1`,
  `trial arm 2`, `trial arm 3` or `trial cancel`. Native J Codex buttons
  provide the same actions.
- New tests cover reward/eligibility protections, player-owned persistence,
  UI serialization, and four additional real NeoForge GameTests.
- **Status: 13/13 user manual tests passed in v1.0.56; 17/17 real GameTests passed.**

### v1.0.55: The Living World (new release, pending player acceptance)

- Six **rare, climate-matched chapter biomes** now join Overworld generation
  using TerraBlender 4.1.0.8. The six dungeons generate in their corresponding
  biome, with themed surface blocks, vegetation, ores and hostile mobs.
- Six **Lost Shrine structures** generate in their matching biomes. Their
  hidden chests offer modest books, candles, paper and materials, never
  legendary weapons, free XP or progression shortcuts.
- J Codex adds **Bestiary** (all six bosses, victory records, counters and drops)
  and **Explore** (biome discoveries and independently tracked shrines).
- New players see a one-time story prologue and short class/combat tutorial.
  Existing progressed players retain their saves and skip the tutorial.
  `/shatteredseals intro` replays instructions at any time.
- Each biome has lightly themed, low-cost ambient particles and one-time
  discovery sound. This update makes **no new player power tiers**.
- **Worldgen migration:** already-explored chunks stay unchanged. The new
  terrain and shrines appear only in *new* chunks. Use a new world to test the
  generation changes before playing on an established save.
- **New dependency:** TerraBlender NeoForge 1.21.1-4.1.0.8
  (CurseForge project 940057, file 6054947), pinned in the modpack manifest.

### v1.0.54: Enchantment and legendary sword polish

- Six legendary sword passives announce a **gold cooldown-start message** in
  chat on activation, then **one green ready-again message** after expiry.
  Their original random chance and independent 40-60 second cooldowns remain.
- Enchanted book tooltips show the true stored enchantment rank (I, II or III)
  even on books bought in v1.0.53 and combined in a vanilla anvil. Maximum
  rank III is clearly marked, without the obsolete upgrade instruction.
- In survival inventory, **pick up the book and left-click compatible gear**,
  or pick up the gear and click its book. An incompatible Codex book cannot
  silently swap positions or be consumed. Anvils still work as before.
  In Creative inventory, use an anvil because the inventory click hooks are
  client-only there.

### Added in v1.0.53: Enchantments, Codex, passives and compatible mods

- **36 new native enchanted books**: 12 Common, 12 Rare, 12 Legendary. Each
  supports I, II and III. J > Enchants provides all book descriptions and
  an XP-level merchant (10 / 20 / 30 vanilla levels). Anvils can apply and
  combine books, and survival inventory left-click of a purchased book onto
  compatible gear is supported.
- **Six rare on-hit legendary sword passives**, each with its own cooldown.
  Dormant, Awakened and Ascended scale the effect, not the trigger frequency.
  The J Codex explains each ability and lists the nine separate relic skills.
- **Story hints**: J > Quest Journal > Reveal rune hint shows the keeper's
  riddle and the current chapter's correct rune sequence.
- **Skill help**: K shows current core ranks and tooltips for all branch
  upgrades, mastery effects and the four class abilities.
- **New CurseForge references**: JEI, Waystones + Balm, ModernFix and FerriteCore.
  We deliberately skip overlapping third-party dungeon, skill, quest and
  enchantment mods. Existing player saves are not reset.
- **First-person guarding** uses a NeoForge item render transform rather than
  the earlier early-stage render event, which should make the actual sword
  block pose visible. This still requires a user screenshot verification.

## v1.0.57: ALL-FEATURES CONSOLIDATED PLAYTEST (development candidate)

**All remaining approved content is staged for one comprehensive acceptance
session**. This combines the originally planned v1.0.57 and v1.0.58 features;
a green build alone will not make it a stable v1.1.0 release.

- **Warden's Sanctuary:** locate in newly generated plains/meadow/forest terrain
  after the first Smuggler King victory via `/shatteredseals sanctuary locate`.
  Seven display/strategy chambers, plus eight ordinary survival rooms and a
  secret arena. Chests are empty, crafting consumes usual resources, portal is
  decorative until you place valid vanilla/modded portals or waystones. Sanctuary
  records and touched blocks are preserved.
- **Shattered Depths:** six chapter-themed optional underground ruin regions
  near Y -32, six rarer puzzle-gated Hidden Vaults, elite enemies with warned
  attacks and once-per-site lore rewards; cave-block seams in new chunks only.
  Use `/shatteredseals depths locate 1` to find the first ruin, or
  `/locate structure shatteredseals:smugglers_tunnels_vault` for a vault.
  The surface campaign and vanilla cave generation remain available.
- **Secret challenge:** earn six Shattered Trial medals in one NG+ cycle,
  travel to the Sanctuary's secret arena, then use Codex > More > Secret or
  `/shatteredseals secret summon`. The Unbound Warden is optional; its
  first-win reward is cosmetic.
- **Sword mastery:** five cosmetic ranks per legendary sword, earned through
  legitimate hostile-mob melee kills; inspect Codex > Swords. No damage tiers,
  proc-rate boosts, or cooldown bypasses.
- **Enchantment messaging:** the eight randomly triggering offensive/healing
  custom enchants now announce a REAL successful proc from the same original
  chance roll. Anti-spam delay 2s; `/shatteredseals enchantmessages off`
  silences notifications. Passive and continuous enchants do not spam chat.
- **Codex:** fixed the Enchants page heading/status collision. Extra pages at
  **J > More Pages**, including Sanctuary, Depths, Secret and Settings.
  Persistent settings toggle the combat HUD, optional particles, enchantment
  notices and optional discovery flavour text. Boss warnings never mute.
- **Enchantments and upgrades:** existing vanilla anvil, shop, direct-book
  application and Dormant/Awakened/Ascended upgrades remain. Upgrading a sword
  now **preserves all already-applied enchantments**, merging each at its
  previous rank or the upgraded sword's higher built-in rank. This removes the
  previous item-replacement loss risk. A standalone consumable safeguard is
  intentionally unnecessary and would add artificial costs.
- **Save safety:** back up the primary world, and test worldgen in a fresh
  world or distant unvisited chunks. Existing generated terrain is never replaced.

**Status:** implementation branch, pending static regression, Java compilation,
real NeoForge GameTests, and the user's final combined playtest. Do not treat
untested interactions, FPS, shaders or full dungeon replay as passed.


## Install the development pack

1. Open [GitHub Actions](https://github.com/danny200028/the-shattered-seals/actions/workflows/build.yml) and choose the latest successful **Build The Shattered Seals** run.
2. Download the artifact named **Shattered-Seals-1.0.57-CurseForge-Modpack**.
3. Extract that artifact ZIP **once**, exposing `The-Shattered-Seals-1.0.57-CurseForge.zip`.
4. Import the inner ZIP through the CurseForge Minecraft modpack importer. CurseForge resolves the pinned NeoForge map and shader dependencies; the custom Shattered Seals JAR lives in `overrides/mods`.
5. Start Minecraft 1.21.1 with Java 21. Use **J** for the Adventurer's Codex, **K** for the skill tree and **L** to toggle the on-screen quest tracker.

The shaderpack is **referenced** as a CurseForge dependency rather than redistributing its proprietary files. If CurseForge does not install it into `shaderpacks`, manually install [Complementary Reimagined](https://www.curseforge.com/minecraft/shaders/complementary-reimagined) and select it in the Iris shader menu. The mod's **own RPG textures** are in the compiled JAR.

### Controls and progression

- **J** Adventurer's Codex: six-chapter journal, next dungeon locator, relic arsenal, Relic Forge, and NG+ mastery.
- **K** Native four-class skill tree.
- **L** Toggle the on-screen chapter tracker with live rune-ward and keeper side-quest progress. The overlay reads server-owned scores and never grants progress.
- **Q / R / V / G** Four combat skill slots. **Important:** Minecraft also binds Drop to Q by default. Before combat, rebind either Drop or Ability 1 in Options > Controls > Key Binds so pressing Q does not drop your held item.
- **Hold right-click with a sword** for 1.8-style frontal sword guarding. A defensive sword pose and temporary BLOCK indicator now show while you hold right-click. Successfully blocked frontal hits produce a shield-clink sound and take 40% less incoming damage. The server validates the weapon, attack direction and short-lived hold signal. Side/rear and environmental damage are unchanged; shields remain vanilla.
- Complete the six dungeon chapters in order by defeating each boss. Summoning sigils spawn in the corresponding loot chest. Dungeon fortresses have six guardian spawners, magma and cobweb hazards, hidden caches and a ritual arena. Each of the six bosses now has distinct particle, audio and actionbar cues at both phase transitions. Dungeon locator uses Minecraft's `/locate structure` to find the current chapter.
- Character level cap is **60**. Clearing the Hollow King and reaching level 60 unlocks New Game+. Later NG+ cycles increase boss Strength and Resistance (capped from cycle four) while repeat boss mastery XP rises from 150 to 300. Each NG+ cycle resets the six rune wards and keeper challenges; completing a chapter's keeper quest awards one new ritual sigil so previously looted fortresses remain replayable.
- Dragon Egg skill resets require explicit confirmation and consume exactly one egg only on approval. Cancellation or closing the screen clears the server approval, and unused confirmations expire after 60 seconds.
- Summoned boss minions are encounter-owned and automatically removed after their associated boss is defeated or disappears, including cleanup of legacy minions in upgraded saves.

### Cleaner survival combat HUD

Abilities **Q and R** are stacked on the left side of the default Minecraft
hotbar, while **V and G** sit on its right. The class label aligns with the
hearts, the resource count aligns with the hunger bar, and the resource meter
spans the space above the vanilla status HUD. In v1.0.54, the class/resource
label sits below Minecraft's action-bar level/XP/SP message and the meter ends
before the hearts/hunger, rather than crowding the text above the hotbar. The panels automatically narrow
for smaller GUI widths and keep server-provided cooldowns / ability names.
**This is a visual client change that still needs an in-game screenshot check.**

### Clearer native RPG menus (v1.0.54)

The four-class skill tree and the Codex now draw their labels after the
vanilla screen widgets/background, with fully opaque text colours. The
game world behind these menus will still be intentionally blurred; menu
labels, branch descriptions, ranks and mastery requirements should remain
sharp and readable. Combat HUD class names are no longer duplicated below
Minecraft's action-bar class text: the active class resource
(e.g. **GUARD 100/100**) is centered above the resource meter. Q/R and V/G
remain on their two sides of the hotbar. These visual changes need a
screenshot confirmation on the user's Minecraft client.

### Responsive controls and sword guard (v1.0.54)

- The ability boxes display **current keybinds from Minecraft's Controls menu**, not fixed Q/R/V/G labels. Changing keyboard or mouse bindings updates the combat HUD immediately, without restarting.
- Right-click sword blocking now visibly raises the first-person sword and displays a temporary BLOCK label outside the vanilla hotbar. When a frontal hit is mitigated, an audible shield-clink confirms it. Damage reduction remains server-owned and requires a sword, an active hold, and an attacker in front.
- The first-person visual is an approximation of classic combat, not an exact 1.8.9 animation. Please compare guarded and unguarded hits in your own client.

### Automated Minecraft testing suite

- The new version includes an automated test collector in the CurseForge import.
- In a **throwaway test world**, type `/shatteredseals audit` to run a read-only
  server-side diagnostic (no cheats required). It checks live dungeon
  registrations, chapter loot, boss functions, NG+ sigils and progression data.
- Close Minecraft. Open the installed CurseForge profile folder (alongside
  `mods` and `saves`) and double-click `TEST_THE_SHATTERED_SEALS.cmd`.
  The Windows collector writes a summary and ZIP to `ShatteredSeals-TestReports`.
- Review the report ZIP before sharing it; filtered logs may include usernames
  or computer paths. Nothing is transmitted automatically.
- GitHub's **Real Minecraft GameTests** workflow spins up a headless
  NeoForge server and runs **17 actual game tests**, covering six dungeon templates,
  NG+ sigil loot and all six bosses' phase transitions and minion cleanup.

Read [the Windows testing guide](docs/AUTOMATED_TESTING.md) for exact steps.
The collector cannot test visual shader quality, full boss AI or an entire
character progression playthrough without a player.

### Release testing

See [the acceptance checklist](docs/PLAYTEST_CHECKLIST.md) before labeling this version stable. It separates automated GitHub build success from actual Minecraft and CurseForge import testing.

### Quality gates and limitations

GitHub Actions automatically generates dungeon structures, runs structural/gameplay regression tests and 17 real NeoForge server GameTests, compiles the Java mod and assembles the release ZIP. **A successful build does not substitute for an in-game test.** Please test fresh-world generation, dungeon placement, monster spawners, server commands, boss encounters, visual UI, shader loading, CurseForge import and save/reload before treating this as a stable release.

Third-party CurseForge projects are referenced by IDs. No proprietary mod/shader binaries are copied into this repository.
