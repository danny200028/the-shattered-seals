"""Regress packaging and safety properties of Windows + in-world runtime diagnostics."""
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
class AutomatedTestingSuite(unittest.TestCase):
    def test_audit_is_registered_and_read_only(self):
        locator=(ROOT/"src/main/java/com/shatteredseals/adventure/DungeonLocator.java").read_text()
        audit=(ROOT/"src/main/java/com/shatteredseals/diagnostics/RuntimeAudit.java").read_text()
        self.assertIn('Commands.literal("audit")',locator)
        self.assertIn("RuntimeAudit.run(",locator)
        self.assertIn("replaySigilRolls",audit)
        self.assertIn("Registries.STRUCTURE",audit)
        self.assertIn("getFunctions()",audit)
        self.assertIn("shatteredseals-tests",audit)
        for destructive in ("giveItem", "setScore(", "clear @s", "addFreshEntity", "performPrefixedCommand", ".setBlock("):
            self.assertNotIn(destructive,audit)

    def test_windows_report_collector_only_reads_worlds(self):
        cmd=(ROOT/"tools/windows/TEST_THE_SHATTERED_SEALS.cmd").read_text()
        ps=(ROOT/"tools/windows/ShatteredSeals-TestReport.ps1").read_text()
        self.assertIn('powershell.exe -NoProfile -ExecutionPolicy Bypass',cmd)
        self.assertIn('shatteredseals-tests\\latest.txt',ps)
        self.assertIn('ZipFile]::OpenRead',ps)
        self.assertIn('Compress-Archive',ps)
        self.assertNotIn('Invoke-WebRequest',ps)
        self.assertNotIn('Invoke-RestMethod',ps)
        self.assertNotIn('Remove-Item',ps)
        self.assertIn('ModLauncher running: args',ps)
        self.assertIn('Launching target .*arguments',ps)
        self.assertIn('[REDACTED_IDENTIFIER]',ps)
        self.assertIn('[USER_PROFILE]',ps)
        self.assertNotIn('Set-ExecutionPolicy',ps)
        self.assertNotIn('Copy-Item -Destination $world',ps)

    def test_curseforge_package_bundles_collector(self):
        packager=(ROOT/"scripts/package_curseforge.py").read_text()
        self.assertIn('overrides/TEST_THE_SHATTERED_SEALS.cmd',packager)
        self.assertIn('overrides/tools/ShatteredSeals-TestReport.ps1',packager)
        self.assertIn('RuntimeAudit.class',packager)

if __name__=="__main__":
    unittest.main()
