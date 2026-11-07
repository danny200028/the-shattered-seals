# The Shattered Seals v1.0.49: In-game Acceptance Checklist

This checklist is **not an automated test result**. The GitHub build compiles the
NeoForge mod, generates six dungeon structures and validates CurseForge packaging.
A tester must mark each of the following steps after actually performing it in
Minecraft Java 1.21.1 (Java 21, NeoForge 21.1.238).

## Automated testing (first run)
- [ ] In a disposable Minecraft world, execute `/shatteredseals audit` without cheats.
- [ ] Confirm the runtime audit reports zero FAIL entries; investigate WARN entries.
- [ ] Close Minecraft; run `TEST_THE_SHATTERED_SEALS.cmd` from the CurseForge profile directory.
- [ ] Check that `ShatteredSeals-TestReports` contains the runtime audit, installed-mod checks and ZIP.
- [ ] Confirm GitHub's Real Minecraft GameTests workflow passes all six GameTests.
- [ ] Review report ZIP before sharing; relevant log extracts may contain personal paths or usernames.

## Installation and graphics
- [ ] Download the `Shattered-Seals-1.0.49-CurseForge-Modpack` GitHub Actions artifact.
- [ ] Extract the outer Actions artifact once; import the inner `The-Shattered-Seals-1.0.49-CurseForge.zip` in CurseForge.
- [ ] Verify the mod loads without crash and the NeoForge mod list includes The Shattered Seals.
- [ ] Verify Xaero's Minimap, Xaero's World Map, Iris and Sodium load on the selected game version.
- [ ] Verify Complementary Reimagined is installed/selected (manual shader installation may be required).
- [ ] Verify custom relic textures and HUD do not show missing textures or collide at smaller GUI scales.
- [ ] Verify Q/R ability cards stack to the **left** and V/G to the **right** of the vanilla hotbar; none may cover a hotbar slot.
- [ ] Verify the class name and resource meter align with and sit **above** the hearts/hunger; test 2x and 3x GUI scale and a narrow window.
- [ ] Verify ability cooldown timers, insufficient-resource tint, and different classes' ability labels remain visible.

## Core gameplay
- [ ] Create a fresh single-player world and verify Hard difficulty applies.
- [ ] Open Codex (J), skill tree (K), and toggle the quest overlay (L).
- [ ] Check quest tracker against Codex chapter, current rune step and keeper side quest.
- [ ] Rebind vanilla Drop or Ability 1 to resolve the default Q key conflict, then test each class and skills (Q / R / V / G as currently assigned).
- [ ] Confirm cooldown/resource indicators reflect server state and persist after save and reload.
- [ ] Test sword right-click guard: damage from straight ahead is reduced, side/rear and environmental damage are not.
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
- [ ] Test saving/loading, leaving/rejoining, and upgrading an existing pre-v1.0.49 world.

## Sign-off
- Automated GitHub unit/static tests: see the latest successful Quick Development Test workflow.
- Automated Java 21 compilation and package verification: see the latest successful Build workflow.
- Six native in-game structure and loot checks: see the Real Minecraft GameTests workflow.
- Your Windows runtime audit and modpack diagnostics: **not verified until collected from your installation**.
- CurseForge import: **not verified until manually checked**.
- Real Minecraft combat, dungeons, shaders and save persistence: **not verified until manually checked**.
- Release status: **development candidate**, not player-tested stable.
