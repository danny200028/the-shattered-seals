# The Shattered Seals: Automated Testing on Windows

This testing toolkit is bundled in the CurseForge import ZIP (no separate
installer, Git, Python or development JDK required).

## One-time installation

1. Download and import the newest **The Shattered Seals** CurseForge ZIP.
2. Start Minecraft using that CurseForge profile, create a **new throwaway test
   world** and join it. Do not use your main survival world for experimental
   gameplay tests.
3. Type **`/shatteredseals audit`** into chat. No cheats are required.
4. Minecraft displays PASS / FAIL / WARN totals. A detailed, **read-only**
   audit is stored in `saves/<world>/shatteredseals-tests/latest.txt`.
5. Close Minecraft. In CurseForge, choose **Open Folder** for the installed
   profile. Double-click **`TEST_THE_SHATTERED_SEALS.cmd`**.
6. Open `ShatteredSeals-TestReports` in the profile root. The collector
   creates a text report and ZIP with the world audit, safe JAR-file
   checks, dependency checks, and relevant excerpts from latest.log.
7. Review the ZIP for personal information before uploading it to ChatGPT.
   The collector does not connect to ChatGPT or upload automatically.

If Windows warns about a locally downloaded script, inspect it first under
`tools/ShatteredSeals-TestReport.ps1`. The launcher uses a PowerShell
execution-policy override for this one process only and changes no policies.

## What is actually tested?

The in-world audit runs on Minecraft's **real integrated server** and verifies:
* The six dungeon structures are registered for world generation
* Boss and relic loot tables are loaded, and every NG+ sigil table actually rolls a matching item
* Each chapter's required boss, ritual and reward functions are available at runtime
* Required level, talent, class and quest scoreboard objectives exist
* Current character / NG+ / rune / side-quest state is within valid bounds
* Chapter miniboss entity types can instantiate
* Basic installed-mod presence and Hard difficulty, flagged as WARN if absent
* Full results are saved to the world folder without altering progression, loot or inventory

The Windows collector additionally checks the **installed JAR itself**,
optional dependencies, shaderpack ZIP presence, logs and crash-report filenames.

`NOT_TESTED` is **not a pass**. Automated diagnostics do not exercise the
six bosses' full attack AI, the physical nine-room dungeon traversal, actual
shaders / GUI rendering, or saving and reloading at each campaign milestone.
These remain in [PLAYTEST_CHECKLIST.md](PLAYTEST_CHECKLIST.md).

## Developer test layers

* `python -m unittest discover -s tests -p 'test_*.py' -v`
  verifies static assets, gameplay functions, resource consistency and the Windows suite.
* GitHub Actions compiles Java 21, generates structures and builds the CurseForge ZIP.
* A separate **Real Minecraft GameTests** workflow launches a headless NeoForge
  GameTest server with the six generated dungeon templates. Twelve tests
  now verify actual arena blocks, spawners, chests, registry entries, ritual
  sigil loot rolls, all six bosses' two phase-change functions, minion spawning
  and minion cleanup. A test-count gate fails CI if fewer than 12 run.
  It runs automatically on GitHub after changes to `main`; no PC setup required.
  It cannot verify visual shader quality or manual boss playthroughs.
* The survival-mode `/shatteredseals audit` performs runtime checks that cannot be
  established by a successful compile.
* No part of this tooling provides a remote-control connection to a player's PC.
