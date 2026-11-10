# The Shattered Seals v1.0.57: ONE ALL-FEATURES PLAYTEST

**This is a manual acceptance checklist, not an assertion that gameplay
has already passed.** Back up saves and use a throwaway world for generation.

## Consolidated final-feature acceptance

- [ ] Install v1.0.57 ZIP and confirm the old v1.0.56 player save still loads
- [ ] J > Books headings never overlap at GUI scales 2 and 3
- [ ] Eight true custom-enchantment procs notify once without flooding chat;
      `/shatteredseals enchantmessages off` mutes optional notifications
- [ ] Each of six sword masteries progresses through five ranks on legitimate
      hostile-mob melee kills, independent of the sword tier or NG+ cycle
- [ ] Locate Warden Sanctuary in newly generated terrain and enter all 15
      connected rooms; use bed, chests, furnaces, anvil, brewing and farm
- [ ] Verify the Sanctuary begins without free loot or equipment and does not
      overwrite any player-modified blocks after restarting/updating
- [ ] Trophy Hall and War Room records match boss kills, NG+ and Trials
- [ ] Explore each underground ruin near the correct surface biome; cave seams
      and chamber placement do not remove vanilla ores or important structures
- [ ] Find and solve one Hidden Vault per chapter using three rune stones,
      then confirm bounded chest loot and one-time puzzle behaviour
- [ ] Defeat an elite in each cave region and test attack warning, saving,
      retreat, first-win reward and inability to farm that same site
- [ ] Win six trials in a single NG+ cycle, summon secret boss INSIDE Sanctuary
      arena, avoid its warned attacks, check death clean-up and trophy persistence
- [ ] Test accessibility settings and confirm required trial warnings persist
- [ ] Complete full six-chapter story, then replay NG+ with classes, swords,
      relics, bounties, shaders and map mods enabled
- [ ] Upgrade all six legendary swords, Dormant to Awakened and Awakened
      to Ascended, with player-applied custom enchants. Verify every original
      enchant and its rank survives, upgrade cost remains unchanged and the
      new sword is not duplicated
- [ ] Retest save/reload, existing world, anvil/enchantment rank upgrades and
      CurseForge import. Record FPS and UI problems for v1.0.59 polish.

---
# Previously verified: v1.0.56 Trials and Bounties

This checklist is **not an automated test result**. The GitHub build compiles the
NeoForge mod, generates six dungeon structures and validates CurseForge packaging.
A tester must mark each of the following steps after actually performing it in
Minecraft Java 1.21.1 (Java 21, NeoForge 21.1.238).

## v1.0.56: Shattered Trials and Bounty Board (13/13 manually passed by player)

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
- [ ] Verify `saves/<world>/shatteredseals-tests/latest.txt` exists after `/shatteredseals audit`.
- [ ] Confirm the clean CurseForge ZIP contains no Windows .cmd or PowerShell .ps1 scripts.
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

## v1.0.65: Rebuilt accessible Sanctuary and mob-free grounds

- [ ] In a NEW disposable test world, enter the Sanctuary and reach both
      the second and third floors using ONLY stairs, not fly/spectator.
- [ ] Examine the three-wide stairs, head clearance, upper-floor landings,
      multiple bridges, curved rooms, non-rectangular exterior and towers.
- [ ] Check all 15 original ground-floor rooms, crafting, chests, Trophy
      Hall masks, holograms and the secret boss arena are still functional.
- [ ] Wait through nighttime in unlit Sanctuary grounds, including outside
      the walls: no natural vanilla mobs should spawn within 12 blocks.
- [ ] Verify player spawned mobs, existing named pets and intended boss
      summons are still permitted.
- [ ] Visit an existing Sanctuary and confirm its storage/building is NOT
      rewritten, while the spawn ward activates automatically.
- [ ] Try a new site with natural underground ore: no incorrect protected
      foundation warning.
- [ ] Save and reopen; check floors/rooms/trophies/chests remain unchanged.

## v1.0.64: Three-floor Sanctuary and themed visuals

- [ ] Place a **new** Sanctuary in a copied world and traverse all three
      floors by stairs, without fly or spectator mode.
- [ ] Check upper bridges, door arches, exterior towers and high greenhouse,
      including boss secret arena and fifteen original ground-floor rooms.
- [ ] Visit a pre-existing Sanctuary and verify no retroactive modification
      to its chest inventories, blocks, markers or trophy displays.
- [ ] Enable the Ashen Covenant pack, and confirm textures, disabled pack,
      resource pack compatibility and shader appearance.
- [ ] Verify new main-menu branding and world-loading art at GUI scales 2/3.
      Singleplayer, Multiplayer, Settings and Quit must work normally.
- [ ] Confirm Complementary Reimagined r5.9.3 loads via Iris, and FPS remains
      acceptable with shaders on/off.
- [ ] Inspect square cover PNG and use the publisher dashboard to set the
      CurseForge project thumbnail separately.
- [ ] Save/reload and verify access to all three floors, storage persistence,
      and unchanged boss, Crown, hologram and secret-arena systems.

## v1.0.63: Sanctuary Trophy Hall holograms

- [ ] In a Sanctuary with all six boss trophies earned, each floating label shows
      the correct **boss name** and **origin dungeon** above the corresponding mask.
- [ ] Labels face the viewer, remain readable with shaders and do not overlap
      neighbouring pedestals or block movement.
- [ ] An unearned boss pedestal has no trophy hologram; earning its mask causes
      the label to appear when visiting the hall.
- [ ] Revisit an existing v1.0.62 Sanctuary: the labels appear automatically,
      without placing a new Sanctuary or altering your saved chests/blocks.
- [ ] Reload the world and revisit the hall repeatedly: no duplicate labels,
      stolen items, modified player builds, or extra trophy rewards.

## v1.0.62: Fixed enchantment book XP-point prices
- [ ] In Survival, check the Codex Books button displays 160 / 550 / 1,395 XP points for Common / Rare / Legendary.
- [ ] At precisely level 10, 20 and 30, purchase a book of the matching rarity and verify no XP remains from an empty bar.
- [ ] With partial progress toward the next level, verify only the stated number of XP points is deducted.
- [ ] Try each purchase just below its corresponding level; ensure no book or XP is lost.
- [ ] Check Creative mode bypasses the book shop XP price.
- [ ] Confirm anvil and inventory book application still work and do not charge the book purchase price a second time.

## Expanded RPG systems (v1.0.53)
- [ ] Open J > Enchants. Read all 36 custom books in the three rarity tabs.
- [ ] Buy Common / Rare / Legendary books at 160 / 550 / 1,395 **XP points** (equivalent to vanilla levels 10 / 20 / 30 from zero).
- [ ] Buy books again at higher starting levels: ensure the XP point cost stays fixed, not a deduction of 10/20/30 displayed levels.
- [ ] Creative book purchases consume zero XP; unaffordable Survival purchases consume zero XP and give no book.
- [ ] Apply previously bought books by direct inventory drag or anvil; no second custom purchase charge.
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


## v1.0.58 focused acceptance (new feature development)

- [ ] Receive Warden's Sanctuary Keystone after first Smuggler King victory, including an existing v1.0.57 save
- [ ] Use it on top of a ground block in a broad clearing: the 81 x 49 structure rests on the clicked surface with foundation support
- [ ] Click near a chest or constructed base: placement safely refuses without consuming the item
- [ ] Visit all 15 rooms, especially the Chronicle Archive in the old treasure vault
- [ ] Check six Chronicle region entries and unique completion trophy, including carried-over elite kills
- [ ] Defeat a fresh underground elite: 30 vanilla XP and 250 RPG XP, awarded only once per site
- [ ] Check original boss soundtrack, six unique 3D boss masks and cinematic titles/particles
- [ ] Check forest exploration and Sanctuary ambient tracks (disable ambience to stop automatic music)
- [ ] Confirm first-person blocking sits higher, while retaining 40% frontal mitigation
- [ ] Reload and retest world progress, trophies, Keystone placement and boss systems

## v1.0.59 Animated Boss Model Acceptance (one combined boss test)

**Back up your survival save first.** Run this alongside the outstanding
v1.0.58 Sanctuary, music, cinematic, Chronicle and Keystone playtest.

- [ ] Smuggler King: new tricorn/coat model, walking, cutlass strike, hurt/death, phase II/III, minion summoning, first-victory mask
- [ ] Cinder Warden: new furnace-headed silhouette, walking, flame attack cues, phase transformations, loot
- [ ] Hollow Knight: new plated armour silhouette, weighted attacks, hurt/death, minions and trophy
- [ ] Arcane Seer: new hovering robe model, floating animation, spell casts and phase transitions
- [ ] Sixfold Guardian: new antlered quadruped, four-leg gait, head-lunge, boss health, loot
- [ ] Hollow King: new crown/spine beast silhouette, tail motion, final phase, minions and loot
- [ ] All six continue to show original cinematic title, soundtrack, healthbar and phase telegraphs
- [ ] Check ordinary Pillagers, Husks, Wither Skeletons, Evokers and Ravagers remain vanilla-looking
- [ ] Check first-victory mask item, Sanctuary 3D trophy and old-save victory flags still work
- [ ] Enable shaders, verify boss model UVs and frame-rate; disable shaders and compare
- [ ] Reopen world and confirm progress, class skills and loot are preserved

The GameTest server validates boss phases, drops and data but **cannot verify
client-side visuals**. Actual model posing, weapons and UV appearance need your
hands-on visual sign-off.

## v1.0.60: Sanctuary terrain-levelled placement hotfix

Use a disposable world or copied save before attempting this irreversible terrain change.

- [ ] Unlock Keystone, find a wooded hillside with non-flat natural dirt/stone and trees
- [ ] First right-click previews the 81x49 footprint and explicitly lists natural blocks to clear
- [ ] Second right-click the same ground block within 10s: terrain/trees inside footprint are cleared, structure is level, stone foundations bridge moderate slopes
- [ ] Verify cliff/unsupported cave placements over 32 blocks deep fail safely with item retained
- [ ] Place a chest or stone-brick block within footprint, click the Keystone: placement fails and blocks remain intact
- [ ] Replaceable natural logs/leaves and user-placed dirt/stone ARE treated as terrain; back up wooden/stone builds before trying
- [ ] Inspect the interior, four battlement towers and glasshouse roof after hillside clearing
- [ ] Confirm one-time placement, saved coordinates and Survival item consumption survive save/reload
- [ ] Combined animated bosses, Chronicle Archive, music and old-world progress remain unaffected


## v1.0.61 Chronicle and Archivist's Crown acceptance

Use a backed-up world. Both original elite rewards and existing old saves need testing.

- [ ] Defeat a new underground elite in Survival, receive a **readable written book** instead of paper; read all three pages and check region history and the correct rune order.
- [ ] Right-click a previously collected named Buried Chronicle paper in the main hand; it is replaced with its readable written book, without another elite fight or extra XP.
- [ ] Complete all six distinct Chronicle regions: the original one-time Crown drops, the **Seventh Leaf** epilogue book is also awarded once, and the Archive chiseled-deepslate marker displays the epilogue.
- [ ] Reopen an old 6/6 save: previous Crown remains in inventory; Seventh Leaf is granted once, with no duplicate Crown on reload.
- [ ] Wear the Crown in the helmet slot; confirm its original gold-and-purple texture, **4 armour / 3 toughness** and knockback resistance. Unequip to check normal defence returns.
- [ ] Wear Crown, defeat a *new* region elite: 30 vanilla XP and **350 RPG/mastery XP**. Unequip and defeat an uncompleted region elite: 30 vanilla XP and **250 RPG/mastery XP**.
- [ ] Revisit a previously completed site and verify no repeat XP, books or crown.
- [ ] Wear Crown within 48 blocks of an unopened, loaded underground hidden vault: actionbar gives X/Y/Z every 10 seconds, plus nearby purple enchantment particles.
- [ ] Unequip Crown, or open the vault: nearby resonance stops.
- [ ] Save/reload, check old Chronicle book pages, Crown equip effects and trophy progress persist.
