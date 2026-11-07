"""The GameTest workflow must prove that all registered tests actually ran."""
import unittest
from pathlib import Path
import importlib.util

ROOT = Path(__file__).resolve().parents[1]
source = ROOT / "scripts/verify_gametest_results.py"
spec = importlib.util.spec_from_file_location("verify_gametest_results", source)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

class RunCountGateTests(unittest.TestCase):
    def test_accepts_twelve_or_more_real_passes(self):
        fake = "12 GAME TESTS COMPLETE IN 3.002 s\nAll 12 required tests passed :)"
        count, result = module.summarize(fake)
        self.assertEqual(count,12)
        self.assertIn("PASS|12",result)

    def test_rejects_skipped_or_partial_suites(self):
        samples = (
            "BUILD SUCCESSFUL in 2s",
            "All 12 required tests passed :)",
            "6 GAME TESTS COMPLETE IN 1s\nAll 6 required tests passed :)",
            "12 GAME TESTS COMPLETE IN 1s\nAll 11 required tests passed :)",
            "12 GAME TESTS COMPLETE IN 1s\nSome tests failed",
        )
        for log in samples:
            with self.subTest(log=log):
                with self.assertRaises(ValueError):
                    module.summarize(log)

    def test_ci_collects_signed_summary(self):
        workflow=(ROOT/".github/workflows/in-game-tests.yml").read_text()
        self.assertIn("Verify all expected tests truly ran", workflow)
        self.assertIn("game-test-summary.txt", workflow)

if __name__ == "__main__":
    unittest.main()
