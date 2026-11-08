# The Shattered Seals: Final Development Roadmap

**Current release:** v1.0.54 (Minecraft Java 1.21.1 / NeoForge, CurseForge).
**Target:** v1.1.0, a complete and balanced single-player Vanilla+ RPG.
**Status:** These features are **planned**, not yet implemented. All items require testing before release.

## Design principles

- Very difficult, but fair: learning a mechanic matters more than grinding more damage.
- No further character-level cap increases beyond level 60; New Game+ remains the late-game progression.
- Avoid adding duplicate third-party mods for systems this pack already owns.
- Cosmetics, navigation, storytelling, rare crafting resources and carefully capped utility beat stat inflation.
- Save compatibility is mandatory; all new worldgen must work on fresh unexplored chunks without rewriting already-generated terrain.

## v1.0.55: The Living World

### Six dungeon-themed biomes

Six existing biome **definitions** are currently packaged but **not placed into the Overworld**. Integrate them into normal, new-chunk Overworld generation and connect each dungeon to the appropriate environment:

| Chapter | Dungeon | New biome | Visual identity |
| --- | --- | --- | --- |
| I | Smugglers' Den | Smugglers' Wilds (`shatteredseals:smugglers_wilds`) | Scarred woodlands, fallen timber, hidden paths and camps |
| II | Cinder Citadel | Cinder Wastes (`shatteredseals:cinder_wastes`) | Ash-grey terrain, basalt, magma fissures and ember particles |
| III | Hollow Crypt | Hollow Moor (`shatteredseals:hollow_moor`) | Foggy marshland, twisted trees, old graves and ruins |
| IV | Arcane Observatory | Arcane Highlands (`shatteredseals:arcane_highlands`) | Wind-carved mountain country, luminous crystals and star motifs |
| V | Sixfold Temple | Sixfold Expanse (`shatteredseals:sixfold_expanse`) | Overgrown ancient stonework, six-element shrines and worn monuments |
| VI | Hollow King's Fortress | Hollow King's Dominion (`shatteredseals:hollow_kings_dominion`) | Darkened woodland, abandoned battlements and corrupted ground |

**Worldgen acceptance criteria:** compatible 1.21.1 NeoForge biome registration and climate/world-generation integration (evaluate TerraBlender versus native alternatives); ensure the existing /locate structure chapter flow works; keep custom biomes rare, with sensible climate/terrain fit; do not replace vast swathes of vanilla geography; make structures actually generate in their biome; validate surface features, vegetation, ambient colours and mob spawns; verify fresh worlds, upgrades and compatibility with Xaero's maps and shaders. Existing explored chunks do not retroactively transform. **No unique high-power loot is granted merely for finding a biome.**

### Boss Bestiary and exploration

- Add Codex bestiary entries for the six bosses: lore, moves, counterplay, first-clear and fastest-clear records, drop previews.
- Discoverable hidden rooms, journals and landmarks link story chapters to their surrounding biomes.
- Rewards: lore, trophies, cosmetics, existing materials and small capped experience, not new damage tiers.

## v1.0.56: Shattered Trials and Adventurer's Bounties

### Shattered Trials: optional dungeon modifiers

- Unlocked after Hollow King and level 60, within New Game+.
- Start with one modifier active per run; a second modifier may unlock in higher NG+ cycles.
- Chapter-specific options: healing restrictions, stronger hazards, new telegraphed attacks and encounter restrictions.
- Separate trial completion record and distinct cosmetic trophies; moderate crafting-resource rewards.
- No inflated legendary weapon tier, infinite XP loop, unavoidable lethal hits or permanent power advantage.

### Repeatable Adventurer's Bounties

- Codex bounty board with monster-hunt, exploration and chapter replay contracts.
- Limit simultaneous contracts and repeat payouts; include varied objectives to avoid single-mob farming.
- Reward existing progression materials, capped XP, trophies and cosmetics. Keep rare enchantment book acquisition meaningful.
- Bounties are optional and cannot bypass chapter requirements.

## v1.0.57: Legendary Equipment

### Five-rank legendary weapon mastery

- Six distinct mastery tracks, one for each existing legendary sword.
- Unlock ranks by using that sword in real combat. Exclude training dummies or trivial entity loops from fast XP farming.
- Mostly cosmetic effects, weapon lore, profile achievements and clear passive feedback.
- At most tiny **capped** utility improvements; do not increase existing passive trigger chances or remove cooldowns.
- Keep Dormant -> Awakened -> Ascended upgrade tiers intact.

### Legendary enchantment protection

- Introduce one rare, consumed-on-use item that **protects a single selected enchantment** when a future reforging or replacement system risks losing it.
- No duplicating books, refunding item costs, removing normal enchanting costs or protecting every enchantment simultaneously.
- If no destructive reforging system is added, avoid a useless item: defer this until it has a concrete protective role.
- Keep normal anvil upgrades and direct-book enchanting unchanged.

## v1.0.58: Final Balance and Refinement

- Audit all 41 enchantment registry entries (36 new shop books plus five older entries), their stacking/compatibility, effects and availability.
- Prevent abusive lifesteal, immortality, resistance and emergency healing combinations; avoid unbounded ability spam.
- Tune bosses with readable visual/audio cues, avoidable damage, fair openings and attack patterns rather than excess health.
- Review level 1-60 pacing, XP costs, NG+ scaling, skill refunds, mastery and shop economy.
- Verify dungeon loot distribution, guaranteed sigils, world exploration, difficulty, performance and save/reload.

## v1.1.0: Complete Release

- Clean CurseForge import with correct NeoForge/Java 21 compatibility and dependency versions.
- Automated Java compilation, package/manifest validation, real server GameTests, and documented player/client checks.
- Fresh-world and **existing-world** acceptance playthrough including all six dungeon biomes, all six dungeon boss fights, save compatibility and NG+.
- Enchantment and inventory interactions at levels I/II/III, on all compatible gear; legendary cooldown messages and screen readability.
- Final changelog, player guide, controls, backup guidance and known limitations.
- **No more major power systems after this release** unless a real gameplay problem demands them.

## Scope control

Avoid extra character levels, more damage rarities, higher legendary sword tiers, unconditional permanent stat bonuses, and piling on overlapping RPG mods. Balance, atmosphere and replayability are the goal.
