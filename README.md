> **Security packaging note (v1.0.61):** Chrome flagged an earlier download that contained
> a Windows .cmd launcher calling PowerShell with an execution-policy bypass.
> The CurseForge ZIP now excludes both Windows scripts entirely. The mod JAR,
> manifest and pinned dependencies are otherwise unchanged. Chrome may still
> flag unsigned mod archives, so do not override a dangerous-download warning
> without independent verification.

# The Shattered Seals ⚔️

Minecraft Java **1.21.1** | **NeoForge 21.1.238** | **Java 21**

The world defaults to Hard difficulty, with high attack speed for an approximation of classic 1.8.9 full-strength combat. Holding right-click with a sword now blocks 40% of frontal, positioned incoming damage while the guard is active. This is not a byte-for-byte recreation of the old combat engine.

A challenging single-player RPG modpack, featuring a six-chapter boss storyline, nine-room natural jigsaw fortresses, class skill trees, powerful relics, rarity-based loot, and New Game+ mastery and repeatable chapter quests.

## Recover custom enchantments with a grindstone (save-safe)

**Fixed extraction recipe for NeoForge 1.21.1:** The previous release asked for a blank book in the second slot, but Minecraft's grindstone rejects plain books. That recipe could not work, even though direct event tests passed.

- Put **one enchanted weapon, tool or armour item** in **either** grindstone input slot. **Leave the other slot empty.** No ordinary book is required or consumed.
- The grindstone output is a **genuine enchanted book** holding one Shattered Seals enchantment at its original level (I, II or III). Taking it **removes only that enchantment from the item**, leaving the item in its original input slot, with its durability, name and other enchantments preserved. Repeat to extract any remaining custom enchantments.
- An item with multiple custom enchants gives **one recovered book per output click**. There's no bonus XP for extraction, no output duplication, no item drops, and no player inventory manipulation. A full inventory prevents shift-click pickup rather than losing the book.
- **Two-item grindstone repairs are unchanged.** An item with no Shattered Seals enchantments uses normal vanilla disenchanting and XP rules. Existing worlds, items, custom Codex books and gear do not need conversion.
- The new GameTests use an **actual GrindstoneMenu** to test whether vanilla accepts the input and gives the recovered book. The older tests had missed the invalid plain-book slot restriction.

## Equipped enchantment stacking and dual-wield melee

- **Four armour-slot enchantments now stack** across the worn set: Guardian Angel (adds Absorption levels), Phoenix Skin (refreshes Fire Resistance I; two or four pieces provide extra Absorption I/II), Lifebloom and Immortal Spirit (add all pieces' maximum-health bonuses). The results share the existing class/NG+ passive aggregator and its cap of effect level V.
- **Dual wield swords or axes** to add compatible offhand enchantments to the attack made with your main-hand weapon. Matching ranks combine to effective rank V, rather than triggering the same proc twice. For example, Soul Edge I + Soul Edge I delivers the equivalent damage of Soul Edge II; IV/V are obtainable as combined equipment power without creating higher-level books.
- Offensive damage enchants retain their vanilla main-hand damage; only the **incremental offhand bonus** is added. Proc enchantments resolve once per successful strike with their original solo proc chances, scaling slightly when copies exist on both weapons. Soul Collector grants bonus vanilla XP for hostile melee kills only, not passive animals.
- Two swords or two axes, or a sword plus an axe all qualify. Shields and ordinary utility offhand items provide no enchantment combat bonus. Bow, harvesting and mining enchantments keep their original uses rather than becoming melee bonuses.
- Existing enchanted gear is used unchanged. Merely equipping the pieces activates stacking; no item migration, rerolling or new books are needed.

## Smugglers' Den pillager crossbow repair (existing worlds safe)

Pillagers from the dungeon's six spawners must spawn with crossbows for their vanilla ranged AI to work. New templates now give them crossbows explicitly. Previously generated Smugglers' Den chunks are also repaired server-side when their unarmed pillagers enter the world, without changing spawner blocks, player-built structures or loot.

## Hostile kill XP, dungeon-only skill milestones (save-compatible)

- **Hostile mobs killed by you anywhere** (Overworld, Nether, End and dungeons) award **5 Shattered Seals character XP** per kill, or the equivalent mastery XP after level 60 in NG+. Monsters outside story dungeons do **not** progress kill milestones.
- **Only hostile dungeon enemies**, including properly tagged bosses, minibosses, elites and their minions, advance the 10/40/100 dungeon-kill milestones and the **100 dungeon-kill requirement** for purchasing a branch Mastery. Old dungeon spawners are recognised by their structure location.
- **Passive animals** such as cows, sheep, chickens and pigs count for neither RPG XP nor milestones. Normal vanilla XP and drops remain unchanged.
- A new `ss_hkills` score counts every player-defeated hostile for XP. The existing `ss_dkills` score is used only for dungeon milestones. The K Skill Tree and chat menus distinguish both.
- Existing worlds retain all skills, purchased masteries, character levels, unspent XP, and previously recorded milestone counts. On first launch of this updated version, the new hostile kill counter is seeded with the old total **and marked as already rewarded**, preventing XP duplication.
- **Important historical limitation:** an earlier short-lived build counted all hostiles in `ss_dkills`. Because the game did not store which old kills occurred in dungeons, those old mixed milestones cannot be perfectly separated. They are preserved rather than unexpectedly reset. **From this update onward, open-world hostile kills never advance milestones.**
- The **Reveal Next Dungeon** locator is unchanged.

## Skill-buff stacking and smooth passive refresh

- Passives from active-class cores, rank-3 talents and mob-kill milestones now **add levels together**. Two separately-earned Haste I bonuses become Haste II. NG+ perk ranks also contribute to the same totals (maximum level V).
- Class-specific effects stop being refreshed after switching classes; existing potion durations may finish naturally. The NG+ perk shop still works across classes.
- All continuously granted passive effects last 16 seconds and refresh at least every 4 seconds. **Night Vision lasts 30 seconds**, staying comfortably above the visual flicker threshold before its next refresh.
- Instant combat-ability buffs still obey their normal durations and cooldowns; these changes do not turn them into permanent effects.
- Level 5 in a talent branch opens the ability to buy its Mastery (character level 50, 100 kills, 8 more skill points). Core skill ranks 4-5 retain their tier-II bonus.

## v1.1.0: Warden's Sanctuary castle rebuild

- Enter through a projecting gatehouse and take the three-wide Grand Stair.
  The signed switchback landing leads to the third-floor keep.
- Chamfered curtain walls, four battlement turrets, a vaulted keep and a glass
  conservatory enclose the castle. Arched passages connect the rounded and
  chamfered ground-floor rooms.
- The 15 functional rooms span three floors. Seven ground-floor spaces
  include the Trophy Hall, portal chamber, garden and secret arena. The
  second floor has the armoury, enchanting workshop, war room, bulk storage
  and brewing nook. Cartography, the Chronicle Archive and the bedroom are
  in the third-floor keep.
- The vanilla spawn ward still covers the Sanctuary and twelve blocks beyond
  its footprint.
- The revised blueprint is used for future Keystone placements. Existing
  Sanctuaries keep their blocks, storage and trophies. To inspect the new
  castle, use a fresh test world or a copied save with a separate, empty site.

## Ashen Covenant rune colours (existing worlds supported)

- Rune puzzle materials are now legible even when the original game would show patinated copper as teal: **Sun = orange-gold chiseled copper**, **Tide = blue prismarine**, **Ash = dark polished blackstone**. Each has its own hand-drawn 16x texture motif.
- The Sixfold Temple's ordinary oxidized-copper trim is **warm burnished copper** in the optional Ashen Covenant resource pack, with a few patina flecks instead of an overall blue-green colour. Copper cannot be mistaken for Tide.
- The journal's optional **Reveal rune hint** now displays the actual block-name-to-symbol mapping without modifying any solution, rune location, quest, world block or existing dungeon.
- To apply to an existing CurseForge install, **replace the older** `The-Shattered-Seals-Ashen-Covenant.zip` in `resourcepacks` with the new ZIP from the latest pack. Keep the pack enabled and press F3+T to reload resources. You do **not** need to rebuild dungeons or restart the story. Other resource packs placed above it can override those textures.

## v1.0.65: The Ashen Covenant fortress and visual identity

- **Three storeys:** connected Gothic Sanctuary with climbable staircases,
  vaulted library rooms, raised bridges, central atrium, a roof keep,
  four battlement towers and a tall glass-roofed garden.
- **Original visual design:** the Ashen Covenant 16x stone-and-dark-timber
  resource pack is included as an optional ZIP; enable in Resource Packs.
  These block textures affect the whole world while enabled.
- **Shaders:** Complementary Reimagined r5.9.3 remains pinned by CurseForge,
  with Iris configuration preselecting it when present.
- **Branded menu and world-load screen:** original art in the first-party JAR,
  and a separate square PNG included for CurseForge listing publication.
- **World safety:** no existing Sanctuary, chest, player decoration, Crown,
  Trophy Hall hologram, or secret arena is automatically overwritten.
  The larger new template appears only with a *new* Keystone placement or
  a manual /place template command in an expendable test world.
- **Important:** the CurseForge listing cover does not change automatically:
  upload its PNG using your project's CurseForge publisher dashboard.

## v1.0.63: Trophy Hall hologram labels

Each earned boss mask in the Warden's Sanctuary Trophy Hall receives a floating,
camera-facing hologram identifying the **boss** and its **dungeon chapter**. The
six displays are automatically labelled as trophies become available. Previously
placed Sanctuaries gain the labels when you enter the hall, without placing a
new Sanctuary or modifying existing storage, structures, trophies or player items.
Labels persist on save/reload and are not duplicated by repeated visits.

## v1.0.62: Fixed XP-point prices for enchantment books

Codex **J > Books** now charges a fixed **160 / 550 / 1,395 actual XP points** for Common / Rare / Legendary books. Those values are the XP needed to reach vanilla levels 10 / 20 / 30 from zero, rather than subtracting that many levels from your current level. Failed Survival purchases give no book and consume no XP; Creative mode bypasses the purchase cost. Anvils and inventory book application retain their existing behaviour without a second custom purchase charge.

## v1.0.61: The Living Chronicle and Archivist's Crown

- **Six readable original written books** replace the old named paper trophies. Read three pages of region history, vault secrets and the true rune order. Right-click a named paper from v1.0.60 to upgrade it to the readable book without repeating a fight.
- **Seventh Leaf epilogue** is a readable bonus volume automatically granted once when the six Chronicle regions are complete, even on an old save with previously completed progress. The Sanctuary Chronicle Archive now reads the extra epilogue in chat on 6/6.
- **Archivist's Crown is now a real wearable legendary helmet**, using the existing item registry ID and saved item. It provides four armour points, three armour toughness, slight knockback resistance and a specially textured gold-and-amethyst open circlet.
- **Crown XP:** while worn, each new site-specific depths elite first victory gives +350 total RPG XP (250 original + 100 bonus), or mastery XP after level 60 in NG+, rather than the original 250. The bonus never triggers twice for the same site.
- **Crown vault sight:** while worn, checks every ten seconds for unopened marked vaults within 48 blocks and gives an actionbar X/Y/Z resonance hint plus enchantment particles. It does not auto-open vaults or spoil progression.
- **Save safety:** preserves all existing six Chronicle bits, old Crown items, one-time rewards, class progress, boss abilities and terrain placement improvements.

Note: Crown resonance only works while the relevant underground vault marker is loaded and within 48 blocks. Back up your save and test a copied world first.

## v1.0.60: Sanctuary terrain placement fix

The Warden's Sanctuary Keystone can now **level ordinary terrain** across its full 81 × 49 footprint: dirt, stone, hills, logs, foliage, plants and water. It clears obstructing natural blocks through the roof height and builds stone-brick foundations down to stable ground (up to 32 blocks). You **no longer need an entirely flat and empty clearing**. Place on the Overworld, right-click once for a preview and clear-count, then click the SAME block again within ten seconds to confirm.

**Safety:** Chests, block entities, crafted stone bricks, machinery, farmland and other recognisable construction blocks are rejected. Dirt, natural stone and logs are indistinguishable from blocks players place, so these **can still be destroyed**. Always back up your world and test the Keystone in a copy before confirming. A sharp drop over 32 blocks is still rejected.

## v1.0.59: Six Animated Bosses

The boss bodies are now original Minecraft-native articulated meshes, not
vanilla creature body models in costumes. All six have bespoke geometry,
original 128x128 texture atlases, animated idle/walking/attack/hurt/death poses,
and tag-driven Phase II / III transformation poses. No added renderer dependency
or GeckoLib requirement. The **underlying vanilla mob entity types and battle
data are intentionally preserved**, so AI, attack damage, quest flags, NG+
modifiers, existing saves, drops and 26 boss/world GameTests still behave
as before. Ordinary vanilla mobs render with their original models.

Read the v1.0.59 playtest checklist for the combined test of these six bosses
and the previous v1.0.58 Sanctuary content. Back up your save.

## v1.0.58: Sanctuary & Legends (automated build and 26 GameTests passed)

**Back up your existing world before installing this update.** v1.0.58 is
available as a CurseForge import ZIP after the GitHub Actions build passes,
but still requires the player's consolidated hands-on survival acceptance.

- **Sanctuary rebuilt:** 15 connected functional rooms within a decorated
  stone-and-dark-oak fortress with four battlement towers, steep gables,
  arched windows and a glasshouse garden. These new structures appear in new
  chunks, or when the new Keystone places a copy. Existing generated buildings
  are not overwritten in explored saves.
- **Sanctuary Keystone:** after the first Smuggler King victory, receive one
  Keystone, even on an old save. Right-click the top of a ground block in an
  81 x 49-block **clear** area. See the corner preview and right-click the
  **same block again within 10 seconds** to confirm placement. The Sanctuary
  starts one block above the clicked surface and a stone foundation bridges
  gaps of up to eight blocks. Chunks must be loaded; unsafe sites, structures,
  liquids and containers prevent placement. Keystone is only consumed after
  successful placement. A replacement can be crafted with a lodestone,
  obsidian and amethyst, but each player can place a Sanctuary only once.
  `/shatteredseals sanctuary locate` reports the placed coordinates.
- **Chronicle Archive:** the Treasure Vault gains a manuscript gallery with
  six readable histories. Defeating all six unique Shattered Depths elites
  records the collection and awards an original-textured Archivist's Crown.
  Existing elite records count automatically; the trophy is not repeatable.
- **Elite rewards:** each uncleared site gives **30 vanilla XP** and
  **250 character RPG XP** (or NG+ mastery XP at the level 60 cap); cannot
  be repeatedly claimed at the same site.
- **Visual upgrades:** six originally textured full-body boss variants and
  six bespoke 3D boss masks, first-victory collectibles and dynamic trophies
  on the Sanctuary Trophy Hall pedestals. Existing vanilla mobs keep normal
  skins. This reuses vanilla creature body rigs rather than replacing all
  six with fully custom animated creature skeletons.
- **Cinematic encounters:** title/subtitle introductions, coloured
  particles and original music cues accompany the six story bosses.
- **Original soundtrack:** three short original Ogg compositions for boss
  encounters, Sanctuary and Shattered biomes. Automatic ambient playback
  respects the existing ambience setting.
- **Sword guarding:** raise the on-screen first-person sword by 0.22 units,
  leaving right-click input and the original 40% frontal damage mitigation
  untouched.

## Locked final development roadmap (originally 8 November 2026; expanded 9 November)

**Feature scope targets v1.1.0**, with explicitly approved additions:
**Shattered Depths** and the **v1.0.58 Sanctuary & Legends** overhaul.
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
Further major expansions beyond this separately approved v1.0.58 update still
require explicit user approval.

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

## Campaign and endgame

- **Warden's Sanctuary:** defeating the Smuggler King unlocks the one-time
  Sanctuary Keystone. Use it in the Overworld at an unoccupied site and
  confirm the placement at the same spot. The castle includes empty storage,
  crafting stations, a garden and the Trophy Hall.
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

## Install v1.1.0

1. Download `The-Shattered-Seals-1.1.0-CurseForge.zip` from the [GitHub release](https://github.com/danny200028/the-shattered-seals/releases/tag/v1.1.0).
2. Import the ZIP in CurseForge and launch the Minecraft 1.21.1 profile with Java 21. The profile uses NeoForge 21.1.238.
3. If updating an existing save, back it up before opening it in the new profile.

Open **Options → Controls → Key Binds** to search for controls with Controlling.
Mouse Tweaks adds inventory shortcuts. AppleSkin shows food and saturation.
If the pack fails to start, confirm the profile uses Java 21 and NeoForge,
then run `/shatteredseals audit` once in-game to check the installed pack.

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

- The import ZIP no longer contains Windows command scripts or PowerShell files. The collector remains an optional, inspectable GitHub source tool.
- In a **throwaway test world**, type `/shatteredseals audit` to run a read-only
  server-side diagnostic (no cheats required). It checks live dungeon
  registrations, chapter loot, boss functions, NG+ sigils and progression data.
- Read the results directly in chat or in your world folder at
  `saves/<world>/shatteredseals-tests/latest.txt`.
- The separate optional Windows collector lives in `tools/windows/` on GitHub
  for advanced troubleshooting only; it is not packaged into CurseForge imports.
- GitHub's **Real Minecraft GameTests** workflow spins up a headless
  NeoForge server and runs **17 actual game tests**, covering six dungeon templates,
  NG+ sigil loot and all six bosses' phase transitions and minion cleanup.

Read [the testing guide](docs/AUTOMATED_TESTING.md) for the built-in audit steps.
Automated tests cannot verify visual shaders, full boss AI or an entire
character progression playthrough without a player.

### Release testing

See [the acceptance checklist](docs/PLAYTEST_CHECKLIST.md) before labeling this version stable. It separates automated GitHub build success from actual Minecraft and CurseForge import testing.

### Quality gates and limitations

GitHub Actions automatically generates dungeon structures, runs structural/gameplay regression tests and 17 real NeoForge server GameTests, compiles the Java mod and assembles the release ZIP. **A successful build does not substitute for an in-game test.** Please test fresh-world generation, dungeon placement, monster spawners, server commands, boss encounters, visual UI, shader loading, CurseForge import and save/reload before treating this as a stable release.

Third-party CurseForge projects are referenced by IDs. No proprietary mod/shader binaries are copied into this repository.
