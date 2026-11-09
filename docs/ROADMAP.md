# The Shattered Seals — LOCKED Final Release Roadmap

> **Scope originally locked: 8 November 2026; explicitly expanded by user approval on 9 November 2026 to include the Shattered Depths underground exploration system.**
> **Current built release:** v1.0.55 (new features pending client playtest; v1.0.54 was player-tested) (Minecraft Java 1.21.1, NeoForge 21.1.238, Java 21, CurseForge).
> **Final target:** v1.1.0, the complete, difficult, single-player Vanilla+ RPG.
> **Implementation status:** v1.0.55 Living World is implemented and its shrine discoveries/loot/audio were player-tested; v1.0.56 onward, including the Shattered Depths, remain PLANNED.
> This document is the canonical feature-freeze checklist. No new major features will be added
> beyond this scope without explicit user approval. Bug fixes, compatibility, accessibility,
> necessary technical implementation, and balance tuning remain permitted.

## Core experience and rules (unchanged)

- Six story chapters and dungeons with six bosses and their legendary swords.
- Four player classes, level cap 60, skill tree, class abilities, relics, quests,
  NG+ mastery and the existing Dragon Egg confirmed-respec rule.
- Keep the classic-style high-speed sword combat and frontal sword guard.
- Keep JEI, Xaero maps, Waystones/Balm, performance mods and shader support.
- **Challenging but fair**: distinct boss moves, telegraphs, positioning and decisions
  are preferred over enormous health bars and random unavoidable damage.
- **No power creep**: no raised level cap, higher weapon tiers, new enchantment
  rarity tier, unlimited healing, cooldown removal, or duplicate RPG mods.
- Normal survival crafting/material costs remain; no base, trophy or exploration
  feature may provide free top-tier gear or an unbounded XP/resource farm.
- Existing saves must be preserved. Backups are recommended for every release.
  New biome terrain appears only in unexplored chunks, not retroactively.

## v1.0.55 — The Living World

**Focus: six connected dungeon biomes, proper opening, discovery and atmosphere.**

### 1. Six rare, naturally generating dungeon biomes

These six biome JSON definitions already exist but are NOT currently integrated
into Overworld biome generation. Implement and test NeoForge 1.21.1 worldgen,
rarity, terrain decoration and matching dungeon structure placement:

| Chapter | Dungeon | Biome | Theme |
| --- | --- | --- | --- |
| I | Smugglers' Den | Smugglers' Wilds (`shatteredseals:smugglers_wilds`) | Scarred woodlands, fallen trees, bandit traces, secret paths |
| II | Cinder Citadel | Cinder Wastes (`shatteredseals:cinder_wastes`) | Ash-grey terrain, basalt, smouldering fissures |
| III | Hollow Crypt | Hollow Moor (`shatteredseals:hollow_moor`) | Fog, twisted trees, mossy graves, abandoned crypt stones |
| IV | Arcane Observatory | Arcane Highlands (`shatteredseals:arcane_highlands`) | High mountains, magical lighting and stargazing landmarks |
| V | Sixfold Temple | Sixfold Expanse (`shatteredseals:sixfold_expanse`) | Ancient overgrowth, sixfold shrines and elemental motifs |
| VI | Hollow King's Fortress | Hollow King's Dominion (`shatteredseals:hollow_kings_dominion`) | Blighted dark woods, ruined battlements and corrupted ground |

Acceptance: rare and climate-appropriate regions; dungeon spawns in its own
region; existing `/locate structure` progression remains reliable; terrain and
features load on a fresh world and unexplored existing-world chunks; shaders
and Xaero mapping still work; no impossible-to-find story chapter or major
vanilla-biome replacement. Avoid arbitrary increases in enemy stats or loot.

### 2. A proper RPG introduction and tutorial

- Short story opening, the six seals premise, class choice and first quest.
- Concise introduction to skills, guarding, Codex, world map and dungeon discovery.
- Skippable or easily dismissed for existing players; no world reset or forced
  class change; respect current class/quest progress on upgrade.

### 3. Boss Bestiary in the Codex

- Six boss pages: story lore, weaknesses/counterplay, telegraphed abilities,
  drops and a reliable defeated/not-defeated status.
- First kill, repeat victories and best times where technically reliable.
- Reveal information through progression; do not spoil late bosses prematurely.
- Only cosmetic/informational completion rewards.

### 4. Exploration secrets and dungeon atmosphere

- Hidden lore fragments, meaningful landmarks, optional discoveries, secret
  rooms and modest non-OP rewards.
- Biome ambience (sound, fog, lighting and particle effects) and boss entrance/
  phase atmosphere, tuned to avoid excessive noise or performance loss.
- No unavoidable visual obstruction, shader incompatibility, or mandatory
  out-of-chapter exploration gate.

## v1.0.56 — Shattered Trials and Adventurer's Bounty Board

**Focus: optional endgame replayability, with limits that prevent farming.**

### 5. Shattered Trials

- NG+ optional dungeon modifiers, unlocked after defeating the Hollow King
  and reaching level 60; separate records for all six chapter dungeons.
- One modifier active initially; two only in a later, balanced NG+ phase.
- Chapter-specific telegraphed attacks, hazards, reduced healing or positioning
  challenges; not cheap one-shot attacks or infinite stat scaling.
- Rewards: cosmetic trial trophies, existing crafting resources and bounded XP.
  No higher sword tiers or guaranteed overpowered gear.

### 6. Adventurer's Bounty Board

- Accessible through the Codex and discoverable at The Warden's Sanctuary
  once the Sanctuary is unlocked.
- Repeatable monster hunts, exploration objectives and dungeon/boss contracts;
  visible tracking and explicit claim flow.
- Limited active contracts, bounded/cooldown-gated reward claims,
  anti-farming controls and level/chapter suitability.
- Rewards use existing materials, modest XP, cosmetic trophies and rare but
  balanced opportunities, never free infinite legendary books.

## v1.0.57 — The Warden's Sanctuary and Legendary Equipment

**Focus: a home worth coming back to, collections and mastery without extra power tiers.**

### 7. The Warden's Sanctuary: permanent progression home base

Build the previously agreed home base with story-gated unlocking, safe access,
save persistence and rooms that open/improve as the adventure progresses:

| Room | Planned purpose | Balance rule |
| --- | --- | --- |
| **Trophy Hall** | All boss trophies, NG+ milestones, Shattered Trials medals, all six biome discoveries, secret boss trophy and other achievements | Decorations and records only |
| **Armoury** | Display and compare six legendary swords and equipment, plus mastery ranks | No duplicated or free weapons |
| **Enchanter's Workshop** | Clear enchanting/book information and access to existing crafting systems | Uses existing XP and material costs |
| **Cartographer's Room** | Discovered biomes, chapter landmarks and dungeon clues | Unlock locations by actually exploring |
| **Treasure Vault** | Trophies and earned resource storage/display | No automatic resource generation |
| **Portal Chamber** | Convenient but limited travel connections, compatible with Waystones | No bypassing chapter gates or free infinite teleportation |
| **War Room** | Boss Bestiary, quest status, bounties and Shattered Trials planning | Information, not combat bonuses |

### Everyday survival facilities (part of the Sanctuary, not an extra power system)

The Sanctuary must also work as the player's real Minecraft survival base,
with **usable blocks and enough practical room** for normal day-to-day play.
Build these as a connected living-and-survival wing or sensible annexes, without
replacing any of the seven specialist rooms:

- **Bedroom:** normal player bed/respawn point and room for personal decorations.
- **Bulk storage room:** ample expandable space for labelled chests, barrels,
  item frames and ordinary Minecraft item organisation, separate from the trophy
  and treasure display vaults.
- **Smelting room:** working furnaces, blast furnaces, smokers and fuel storage.
- **Crafting workshop:** crafting tables, anvils, smithing tables, grindstones
  and stonecutters; alongside the separate Enchanter's Workshop.
- **Brewing nook:** brewing stands, cauldrons and potion ingredient storage.
- **Garden and farm:** room for crops, food supplies and a modest animal area,
  preferably in an outdoor courtyard or attached garden.

All facilities use standard vanilla block mechanics, storage ownership and
crafting/fuel costs. Offer expansion space so survival storage does not become
cramped. **No free auto-generated resources, item duplication or unearned gear.**
Practical facilities are available early enough for everyday use; high-end
cosmetic rooms and records continue unlocking through story progression.
Make sure player bed spawn points, inventories and placed blocks persist on
save/reload and NG+, and avoid overwriting player-modified rooms on updates.

The home **visually evolves** after chapter victories and optional milestones.
Preserve completed displays and achievements across save/reload and NG+.
Integrate the achievements and trophy collection **inside this base**, not as
a redundant separate power system. Add no unconditional sanctuary buffs.

### 8. Achievements and trophies

- Six boss trophies, legendary sword mastery awards, biome exploration
  collection, NG+ and trial achievements, and a secret encounter centrepiece.
- The Sanctuary Trophy Hall is the physical display; the Codex provides
  a searchable checklist/record.
- Cosmetics, titles, banners and furniture only, not extra damage/armour.

### 9. Five-rank legendary sword mastery

- One mastery track for each of the six existing legendary swords.
- Gain ranks through legitimate combat with that specific weapon.
- Mainly visual trails, weapon lore, titles, showcase effects and records.
- At most minimal capped utility if later balance testing demonstrates a need.
- **Do not increase passive proc chance, remove cooldowns, add damage tiers,
  or bypass Dormant/Awakened/Ascended progression.**
- Anti-exploit progress rules for low-risk/trivial-mob farming.

### 10. Legendary enchantment protection

- One rare, consumable safeguard for **one chosen enchantment** if a future
  equipment-replacement/reforging mechanic would otherwise remove it.
- Never duplicate enchantments, refund all costs or protect unlimited slots.
- **Do not add a pointless item**: if the complete design never risks losing
  enchantments, satisfy this requirement by explicitly confirming existing
  safe upgrades and omit a standalone protection item.
- Preserve vanilla anvils, direct book application and ranks I/II/III.

## v1.0.58 — Shattered Depths, Final Endgame Encounter, Settings and Combat Polish

### 11. One secret endgame challenge

- Optional, unlocked through the six Shattered Trials, not required to finish
  the main story.
- A distinct high-skill challenge with readable mechanics, meaningful counterplay,
  safe recovery after failure, and no unfair instant-kill gimmicks.
- Cosmetic-only completion rewards and an exclusive Sanctuary centrepiece.
- Reuse existing progression, avoid inventing a seventh mandatory power tier.

### 12. Modpack settings and accessibility

- Player-facing options for HUD layout, ability/cooldown notices, boss text
  prompts, ambient effects and optional performance-friendly visual settings.
- Preserve core damage/difficulty rules; never provide a hidden easy-mode buff
  through settings. Respect keybind remapping and readable UI scales.

### 13. Combat and atmosphere refinement

- Boss visual/audio telegraphs, hit feedback, phase transitions and reliable
  encounter cleanup; tune with client visual/performance testing.
- Ensure the Sanctuary, dungeon biomes, soundtrack cues (original or properly
  licensed only), quests and Codex feel coherent and do not spam chat.

### 14. The Shattered Depths: optional underground RPG exploration (user-approved 9 November 2026)

**Status: PLANNED, not implemented in v1.0.55.** Give the Overworld its own
subterranean exploration layer, using vanilla cave/mining as the foundation.
Create six distinct underground regions related to the six chapter biomes,
without forcing players underground to clear the main story:

| Surface chapter biome | Underground region | Distinct exploration |
| --- | --- | --- |
| Smugglers' Wilds | **Smugglers' Tunnels** | Abandoned contraband shafts, false walls, hidden stores |
| Cinder Wastes | **Emberfault Chasms** | Basalt caves, magma vents, telegraphed heat hazards |
| Hollow Moor | **Drowned Ossuary** | Flooded passages, crypt remains, skeletal ambushes |
| Arcane Highlands | **Starshard Caverns** | Rare luminous geodes, crystal bridges, arcane anomalies |
| Sixfold Expanse | **Rootbound Grottos** | Overgrown caves, strange roots, sixfold inscriptions |
| Hollow King's Dominion | **Black Crown Depths** | Corrupted deepslate, catacombs and dangerous sentries |

**Generation and discovery**
- Integrate actual underground terrain/decoration features and cave-region
  selection into NeoForge 1.21.1 Overworld generation; avoid simply recolouring
  surface ground or placing a facade of blocks above ground.
- Preserve vanilla caves, stone types, ores, strongholds, trial chambers and
  mining access. Keep rare regions appropriately spaced and traversable;
  do not sever crucial vanilla resources or existing dungeon structures.
- Add natural underground ruins, buried catacombs, crystal chambers, small
  encounter sites and especially rare **Hidden Vaults** with discernible
  entrances or discoverable clues. Generate below ground and do not overwrite
  existing terrain in previously explored chunks.
- Introduce underground-exclusive creature encounters and a small set of
  optional, mechanically distinct **elite/miniboss** fights, with readable
  telegraphs and retreat paths. Main six campaign bosses remain unchanged.
- Distinctive sound/particle/lighting atmosphere must be configurable and
  performant with shaders and Xaero's maps, with no forced darkness, excessive
  audio spam or unavoidable lethal traps.

**Rewards, secrets and progression**
- Provide balanced, bounded cave chest loot, rare **Starshard** and other
  underground materials where justified, collectible lore fragments and
  simple optional vault clue/puzzle interactions. New resources may serve
  cosmetic recipes and existing forging/crafting costs, not a stronger
  equipment tier or a replacement for normal mining.
- Tie underground lore to the corresponding six chapter histories, with
  J > Explore tracking distinct cave discoveries, vaults and collectibles.
  Sanctuary Cartographer/Trophy Hall should reflect earned records once built.
- Keep all underground content optional. No mandatory dungeon summons,
  sigils, boss keys or level-60 progression may be gated behind mining;
  never give free legendary weapons, unlimited XP or infinite resources.
- Respect saves, single-player discovery flags, one-time rewards and NG+
  rules; preserve existing resource, combat, enchantment and class balances.

**Acceptance criteria before v1.0.59 feature freeze**
- On fresh worlds, independently locate and enter all six underground regions,
  with thematic structures and no broken chunk edges, water/lava floods or
  untraversable generated rooms. Validate rare/ordinary generation rates.
- Confirm underground ruins, catacombs, vaults, loot tables, secret mechanisms,
  optional elite encounters and Codex counters each work and persist correctly.
- Repeat generation in unexplored chunks of an upgraded save; confirm existing
  explored chunks, vanilla cave/ore content and all six surface dungeons remain
  intact. Test save/reload, shaders, multiplayer-safe data handling and FPS.
- Add automated feature/structure/loot assertions and actual server GameTests
  for underground structure placement and interactions. Finish with manual
  survival and shader playtesting; green GitHub checks alone do not sign off.

## v1.0.59 — Feature Freeze, Balance and Release Candidate

**No new major gameplay systems after v1.0.58.** The user-approved Shattered Depths expansion must be completed and tested before the feature freeze.

- Audit all **41** custom enchantment registry entries (36 shop additions
  and five older entries): real effects, tooltip correctness, conflicts and
  stacking, levels I-III, anvil and inventory application.
- Cap abusive damage, lifesteal, emergency survival, resistances and resource
  generation. Test all classes and legendary swords together at endgame.
- Review level 1-60 progression, mastery economy, NG+ scaling, boss difficulty,
  bounties, Shattered Trials, XP purchase prices and loot drop fairness.
- Ensure full campaign remains possible without farming bounties or trials.
- Validate aboveground and underground worldgen, Sanctuary persistence, relics, Boss Bestiary records,
  secret challenge unlocking, fresh worlds, save updates and survival inventory.
- Fix compatibility, performance, missing assets, crashes and exploits.

## v1.1.0 — COMPLETE Release

- Export one reproducible CurseForge-importable package for Java 1.21.1/
  NeoForge with verified dependency pins, bundled mod textures and documentation.
- Pass automated Java compile, asset/data/packaging checks and real server GameTests.
- Perform full **client** acceptance on fresh and upgraded saves: surface/underground exploration,
  all six dungeons/bosses, all classes, Level 60/NG+, bounties, Shattered Trials,
  Sanctuary rooms/trophies, enchantments, all six legendary sword powers,
  secret encounter, map/mod/shader integration, save and reload.
- Deliver final controls tutorial, progression guide, troubleshooting,
  backup instructions, changelog and honest known-limitations section.
- Mark stable only after real-player checks are complete, not just green CI.

## Locked scope checklist

- [ ] Six dungeon biomes naturally generated and linked to the six dungeons
- [ ] RPG introduction and optional tutorial
- [ ] Six-boss Codex Bestiary
- [ ] Environmental secrets and dungeon atmosphere
- [ ] Shattered Depths: six subterranean regions, underground ruins/catacombs, optional elite encounters, Hidden Vaults, loot, Codex discoveries and save-safe generation
- [ ] Shattered Trials
- [ ] Repeatable Adventurer's Bounty Board
- [ ] The Warden's Sanctuary with all seven rooms
- [ ] Sanctuary Trophy Hall, achievements and collectible displays
- [ ] Five-rank mastery for all six legendary swords
- [ ] Meaningful single-enchantment protection, or safe-upgrade confirmation
- [ ] One optional secret endgame encounter and its trophy
- [ ] Modpack UI/notification/ambience settings
- [ ] Comprehensive final balance and regression tests
- [ ] Verified v1.1.0 CurseForge stable release

These boxes remain unchecked until implemented and both automated and client playtests pass. The Shattered Depths
is the user-approved scope exception dated 9 November 2026. Any further major additions
still require explicit approval; fixes and technical choices that satisfy an existing item do not.
