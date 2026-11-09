# The Shattered Seals v1.0.56: In-game Acceptance Checklist

This checklist is **not an automated test result**. The GitHub build compiles the
NeoForge mod, generates six dungeon structures and validates CurseForge packaging.
A tester must mark each of the following steps after actually performing it in
Minecraft Java 1.21.1 (Java 21, NeoForge 21.1.238).

## v1.0.56: Shattered Trials and Bounty Board (not yet player-confirmed)

Use a disposable Creative/Survival test world or a backup of a fully progressed
save. v1.0.55 surface worlds and prior save progression must remain intact.

- [ ] Install `The-Shattered-Seals-1.0.56-CurseForge.zip`, launch on Java 21 / NeoForge 21.1.238,
      and confirm J has Trials and Bounty tabs with readable text at 2x/3x GUI scales.
- [ ] Before level 60 / Hollow King victory / NG+, Trials cannot be armed even with commands.
- [ ] At level 60, Hollow King won and an active NG+ chapter, choose Hazard Pulse;
      summon the proper boss at its natural arena. Confirm a clear hazard warning
      before a distinct chapter-specific effect, no surprise unavoidable instant kills.
- [ ] Choose Scarcity for another eligible chapter and verify healing is halved
      **only while its trial boss is nearby**, never outside combat.
- [ ] Check combined modifiers are blocked until NG+ cycle 3, and one medal per
      chapter per cycle cannot be farmed from repeat boss summons.
- [ ] Confirm trial failure/death and cancel never generate medals or duplicate
      items; NG+ cycle progression permits legitimately new chapter medals.
- [ ] Accept a Hunt bounty, defeat the correct ordinary mobs, and verify tracked
      progress increases but not from scripted minions or named mobs.
- [ ] Accept a Survey bounty and visit the correct biome; verify it completes
      without cheating via a previously explored unrelated biome.
- [ ] Accept a Guardian bounty; defeat the correct chapter boss; only that
      chapter's encounter counts.
- [ ] Check only one bounty active, no claims before completion, explicit claim
      gives modest XP/ordinary resources, and the 10-minute claim cooldown works.
- [ ] Verify abandon gives no reward; save/reload/death preserve active progress
      and earned record counts. Verify no creative/admin commands are required.
- [ ] Open and close the Codex repeatedly; controls never grant items by themselves.
- [ ] Existing combat, boss loot, dungeon progression, NG+, biome discovery,
      lost shrines and shaders remain functional.

## v1.0.55: The Living World (prior release regression checks)

**Use a new test world for natural worldgen**, and back up your original save
before checking existing-world compatibility.

- [ ] CurseForge imports `The-Shattered-Seals-1.0.56-CurseForge.zip`
      and installs TerraBlender NeoForge **1.21.1-4.1.0.8** automatically.
- [ ] A new world starts on Java 21 / NeoForge 21.1.238 without registry
      errors. The one-time Shattered Seals introduction appears for a new player.
- [ ] Existing-world characters retain quests, levels, class, bosses, items,
      achievements and familiar terrain, without replaying the introduction.
- [ ] `/shatteredseals intro` replays the tutorial without resetting progress.
- [ ] In the new world find each of six chapter biomes in new terrain
      using `/locate biome shatteredseals:<biome_name>`.
- [ ] Check biome surfaces, resource ores, trees/vegetation, normal hostile mobs,
      atmospheric particles, map/shaders and that vanilla regions still exist.
- [ ] `/locate structure shatteredseals:<dungeon_name>` finds the matching
      dungeon in its themed biome; J > Journal locator still works.
- [ ] Find one lost shrine with `/locate structure
      shatteredseals:smugglers_wilds_lost_shrine` and inspect its modest chest.
- [ ] Approach the shrine: first discovery announces lore once, survives reload
      and increases the J > Explore shrine count.
- [ ] Visit one biome: first discovery announces once and increases its
      J > Explore counter; re-entering cannot repeatedly award discoveries.
- [ ] J > Bestiary correctly displays six bosses, victory counts, advice and
      chapter unlock states without prematurely spoiling future chapters.
- [ ] J > Explore shows six biome entries and six independent shrine statuses.
- [ ] The six existing dungeons/bosses, swords and enchantment systems
      still work; World Discovery does not grant free combat advantages.
- [ ] Test first-time client menus at 2x/3x GUI scale, including new Bestiary
      and Explore tabs; no tab overlap, text clipping or missing textures.
- [ ] Verify FPS stays stable with shaders and effects, including in shrines.

## Automated testing (first run)
- [ ] In a disposable Minecraft world, execute `/shatteredseals audit` without cheats.
- [ ] Confirm the runtime audit reports zero FAIL entries; investigate WARN entries.
- [ ] Close Minecraft; run `TEST_THE_SHATTERED_SEALS.cmd` from the CurseForge profile directory.
- [ ] Check that `ShatteredSeals-TestReports` contains the runtime audit, installed-mod checks and ZIP.
- [ ] Confirm GitHub's Real Minecraft GameTests workflow passes all six GameTests.
- [ ] Review report ZIP before sharing; relevant log extracts may contain personal paths or usernames.

## Installation and graphics
- [ ] Download the `Shattered-Seals-1.0.56-CurseForge-Modpack` GitHub Actions artifact.
- [ ] Extract the outer Actions artifact once; import the inner `The-Shattered-Seals-1.0.56-CurseForge.zip` in CurseForge.
- [ ] Verify the mod loads without crash and the NeoForge mod list includes The Shattered Seals.
- [ ] Verify Xaero's Minimap, Xaero's World Map, Iris and Sodium load on the selected game version.
- [ ] Check JEI, Waystones + Balm, ModernFix and FerriteCore all install and load successfully.
- [ ] Use JEI's recipe overlay and Waystones; confirm no resource loading conflicts.
- [ ] Verify Complementary Reimagined is installed/selected (manual shader installation may be required).
- [ ] Verify custom relic textures and HUD do not show missing textures or collide at smaller GUI scales.
- [ ] Verify Q/R ability cards stack to the **left** and V/G to the **right** of the vanilla hotbar; none may cover a hotbar slot.
- [ ] Verify the class name and resource meter align with and sit **above** the hearts/hunger; test 2x and 3x GUI scale and a narrow window.
- [ ] Verify the vanilla action-bar level/XP/SP line sits above the custom class/resource labels with no text overlap; the resource bar must still end before the hearts/hunger.
- [ ] Verify ability cooldown timers, insufficient-resource tint, and different classes' ability labels remain visible.

## Targeted v1.0.54 regression tests
- [ ] Proc each legendary sword: confirm one gold cooldown-start notice
      and one green available-again notice after 40/45/50/55/60 seconds.
- [ ] Switch swords while a passive cools down: no duplicate notices
      or premature activation. Save/reload during the cooldown.
- [ ] Hover a combined Dragon's Fury II and III enchanted book.
      Verify the tooltip says Level II / III and Level III / III.
- [ ] Check existing v1.0.53 books still display correctly without
      buying replacements.
- [ ] In Survival inventory, click a Dragon's Fury III book onto a
      Netherite sword; also try the reverse (sword onto book).
- [ ] Click an incompatible sword enchantment onto a pickaxe. The
      book must not be consumed or swap with the pickaxe.
- [ ] Verify the vanilla anvil still applies and combines books as before.

## Expanded RPG systems (v1.0.53)
- [ ] Open J > Enchants. Read all 36 custom books in the three rarity tabs.
- [ ] Buy a common book with 10 vanilla XP levels, rare with 20, legendary with 30.
- [ ] Attempt an unaffordable purchase; verify no XP is consumed.
- [ ] Apply a book through an anvil, then combine matching level I books into II,
      and matching level II books into III, without exceeding maximum level III.
- [ ] Put a book on the cursor and left-click it onto compatible equipment in
      the survival inventory. Verify incompatible items do not consume books.
- [ ] Test each sword's unique passive at Dormant, Awakened and Ascended tiers.
      Confirm procs are rare, randomized, do not chain infinitely, and share
      cooldown even when switching between identical copies of a sword.
- [ ] Read rune order hints in J; verify hint matches all six chapter keepers.
- [ ] Read skill and mastery tooltips in K and inspect core ranks 0-5.
- [ ] Confirm first-person sword position actually changes while guarding.

## Native UI clarity (v1.0.54)
- [ ] Open the skill tree with K. The world behind the menu may be blurred,
      but branch labels, ranks, mastery descriptions and title should be sharp.
- [ ] Open the Codex with J and verify all quest/forge/mastery descriptions are crisp.
- [ ] Verify no duplicate class label beneath the vanilla level/SP action bar.
- [ ] Verify GUARD/FURY/FOCUS/ARCANE 100/100 is centered over the meter,
      while four ability panels remain anchored outside the hotbar.
- [ ] Change GUI scale and check menu text and button hitboxes.

## Core gameplay
- [ ] Create a fresh single-player world and verify Hard difficulty applies.
- [ ] Open Codex (J), skill tree (K), and toggle the quest overlay (L).
- [ ] Check quest tracker against Codex chapter, current rune step and keeper side quest.
- [ ] Rebind vanilla Drop or Ability 1 to resolve the default Q key conflict, then test each class and skills (Q / R / V / G as currently assigned).
- [ ] Confirm cooldown/resource indicators reflect server state and persist after save and reload.
- [ ] Hold right-click with a sword: the first-person sword pose should visibly change and a temporary BLOCK label appear beside the hotbar.
- [ ] Take a hit from a mob directly ahead while guarding: confirm the clink sound and reduced damage compared with an unguarded hit; rear/side attacks should not be reduced.
- [ ] Rebind Ability 1 from Q to another free key in Options > Controls, and verify the HUD button displays the new key immediately.
- [ ] Rebind a second ability to a mouse button, then restart Minecraft to verify that its HUD label persists.
- [ ] Verify a normal skill respec requires confirmation and consumes **exactly one** Dragon Egg.
- [ ] Cancel or close a reset confirmation and confirm no Dragon Egg is consumed and no approval remains; verify the 60-second expiry.
- [ ] Verify refund of regular spent points and invested New Game+ mastery allocations.

## The six-chapter campaign
For each chapter: Smugglers' Den, Cinder Citadel, Hollow Crypt, Arcane
Observatory, Sixfold Temple and Hollow King's Fortress:
- [ ] Confirm the fortress generates in the target biome and the locator finds it.
- [ ] Walk all nine connected chambers without blocked access to the boss arena.
- [ ] Confirm six encounter spawners, hazards, optional treasure and one sigil chest work.
- [ ] Speak to the chapter keeper and complete its miniboss challenge.
- [ ] Resolve the three runes in the keeper's stated order; wrong sequence must not unlock the ward.
- [ ] Confirm attempting to summon the boss before unlocking the ward fails.
- [ ] Confirm boss summoning only works at its matching arena and consumes one sigil.
- [ ] Confirm boss health bar, three phases, distinct warning effects and minions.
- [ ] Kill or safely remove each boss and confirm its encounter-owned minions are cleaned up.
- [ ] Confirm boss victory advances the story and awards expected chapter-specific loot.
- [ ] Confirm optional cache rarity and relic upgrade recipes behave as described.

## Progression and repeated play
- [ ] Level progression stops at 60 and NG+ stays locked before the Hollow King victory.
- [ ] Start NG+ and verify rune puzzles and side quests reset across all six chapters.
- [ ] Visit a previously looted dungeon in NG+, complete the keeper quest and receive a new matching sigil.
- [ ] Replay all six bosses in the correct order without first-clear rewards being repeated.
- [ ] Compare boss strength and mastery XP in NG+ cycles 1, 2 and 4: scaling must increase and then cap.
- [ ] Test saving/loading, leaving/rejoining, and upgrading an existing pre-v1.0.54 world.

## Sign-off
- Automated GitHub unit/static tests: see the latest successful Quick Development Test workflow.
- Automated Java 21 compilation and package verification: see the latest successful Build workflow.
- Thirteen native in-game structure, phase and guard-facing checks: see the Real Minecraft GameTests workflow.
- Your Windows runtime audit and modpack diagnostics: **not verified until collected from your installation**.
- CurseForge import: **not verified until manually checked**.
- Real Minecraft combat, dungeons, shaders and save persistence: **not verified until manually checked**.
- Release status: **development candidate**, not player-tested stable.
