#!/usr/bin/env python3
"""Fail CI if the GameTest server exited without running our required scenarios.

A green Gradle task is not sufficient evidence: some launcher configurations
can skip test registration entirely. Require a real suite completion marker
from Minecraft's own GameTestServer.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

MINIMUM_TESTS = 12


def summarize(log: str, minimum: int = MINIMUM_TESTS) -> tuple[int, str]:
    done = re.findall(r"\b(\d+) GAME TESTS COMPLETE\b", log)
    successes = re.findall(r"\bAll (\d+) required tests passed\b", log)
    if not done or not successes:
        raise ValueError("Missing server evidence that the full GameTest suite finished and passed")
    completed, passed = int(done[-1]), int(successes[-1])
    if completed != passed:
        raise ValueError(f"GameTest results disagree: completed={completed}, passed={passed}")
    if passed < minimum:
        raise ValueError(f"Expected at least {minimum} real GameTests, only {passed} passed")
    return passed, f"PASS|{passed} real NeoForge GameTests executed and passed (minimum {minimum})"


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("Usage: verify_gametest_results.py <game-test-log>")
    path = Path(sys.argv[1])
    try:
        count, result = summarize(path.read_text(errors="replace"))
    except ValueError as exc:
        raise SystemExit(f"FAIL|Minecraft test verification: {exc}") from exc
    print(result)
    Path("game-test-summary.txt").write_text(
        "THE SHATTERED SEALS: VERIFIED GAME TEST RUN\n"
        + result + "\n"
        + "NOT_TESTED|Visual shaders, actual interactive UI and full campaign playthrough\n"
    )


if __name__ == "__main__":
    main()
