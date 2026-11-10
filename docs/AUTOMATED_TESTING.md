# The Shattered Seals: Automated Testing on Windows

The built-in /shatteredseals audit is part of the CurseForge modpack.
The optional Windows report collector remains as source under tools/windows
on GitHub; it is **not** bundled in the gameplay download. This keeps the
download free of Windows launcher and PowerShell scripts.

## One-time installation

1. Download and import the newest **The Shattered Seals** CurseForge ZIP.
2. Start Minecraft using that CurseForge profile, create a **new throwaway test
   world** and join it. Do not use your main survival world for experimental
   gameplay tests.
3. Type **`/shatteredseals audit`** into chat. No cheats are required.
4. Minecraft displays PASS / FAIL / WARN totals. A detailed, **read-only**
   audit is stored in `saves/<world>/shatteredseals-tests/latest.txt`.
5. Open the world folder at
   `saves/<world>/shatteredseals-tests/latest.txt` and inspect the results.
6. If you need the optional Windows diagnostic collector, inspect its source
   under `tools/windows/` in the GitHub repository and use it separately.
   It is **never required** to install, play or test the CurseForge pack.
7. Review any audit file for personal information before sharing it.
   Nothing is transmitted automatically.

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
It excludes launcher command lines (which can contain account IDs), redacts
Windows user-profile paths and known credential markers before exporting
relevant log excerpts. Always review exported reports before sharing them.

`NOT_TESTED` is **not a pass**. Automated diagnostics do not exercise the
six bosses' full attack AI, the physical nine-room dungeon traversal, actual
shaders / GUI rendering, or saving and reloading at each campaign milestone.
These remain in [PLAYTEST_CHECKLIST.md](PLAYTEST_CHECKLIST.md).

## Developer test layers

* `python -m unittest discover -s tests -p 'test_*.py' -v`
  verifies static assets, gameplay functions, resource consistency and the Windows suite.
* GitHub Actions compiles Java 21, generates structures and builds the CurseForge ZIP.
* A separate **Real Minecraft GameTests** workflow launches a headless NeoForge
  GameTest server with the six original dungeons plus the generated final-feature structures. Twenty-seven real GameTests
  verify existing arenas and safeguards alongside actual generation and block
  placement for the Sanctuary, six underground ruins, a hidden vault and the
  new secret challenge/mastery gates. The test-count gate rejects fewer than 26.
  It runs automatically on GitHub after changes to `main`; no PC setup required.
  It cannot verify visual shader quality or manual boss playthroughs.
* The survival-mode `/shatteredseals audit` performs runtime checks that cannot be
  established by a successful compile.
* No part of this tooling provides a remote-control connection to a player's PC.
