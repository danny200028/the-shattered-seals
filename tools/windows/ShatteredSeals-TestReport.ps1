# The Shattered Seals Windows test reporter.
# Runs entirely offline, reads only this CurseForge profile and writes a shareable ZIP.
# It never edits Minecraft worlds, player saves, items, or settings.
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$profileDir = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$outputRoot = Join-Path $profileDir 'ShatteredSeals-TestReports'
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$bundleDir = Join-Path $outputRoot ("Report-" + $stamp)
$zipPath = Join-Path $outputRoot ("ShatteredSeals-Report-" + $stamp + ".zip")
New-Item -ItemType Directory -Path $bundleDir -Force | Out-Null
$messages = New-Object System.Collections.Generic.List[string]
$passed = 0
$failed = 0
$warned = 0
$unverified = 0
function Add-Result([string]$status, [string]$description) {
    $script:messages.Add($status + '|' + $description)
    switch ($status) {
        'PASS' { $script:passed++ }
        'FAIL' { $script:failed++ }
        'WARN' { $script:warned++ }
        'NOT_TESTED' { $script:unverified++ }
    }
}
try {
    $modsDir = Join-Path $profileDir 'mods'
    Add-Result 'PASS' 'Windows PowerShell collector started successfully'
    if (-not (Test-Path $modsDir -PathType Container)) {
        Add-Result 'FAIL' 'No mods folder found. Place and run this tool inside the imported CurseForge profile.'
    } else {
        $modJars = @(Get-ChildItem -LiteralPath $modsDir -File -Filter 'the-shattered-seals-*.jar' -ErrorAction SilentlyContinue)
        Add-Result $(if ($modJars.Count -eq 1) { 'PASS' } else { 'FAIL' }) ('Exactly one first-party mod JAR installed: ' + $modJars.Count)
        if ($modJars.Count -eq 1) {
            Add-Type -AssemblyName 'System.IO.Compression'
            Add-Type -AssemblyName 'System.IO.Compression.FileSystem'
            $zip = [System.IO.Compression.ZipFile]::OpenRead($modJars[0].FullName)
            try {
                $names = @{}
                foreach ($entry in $zip.Entries) { $names[$entry.FullName] = $true }
                foreach ($part in @(
                    'META-INF/neoforge.mods.toml',
                    'com/shatteredseals/diagnostics/RuntimeAudit.class',
                    'com/shatteredseals/client/SkillTreeScreen.class',
                    'com/shatteredseals/client/QuestTrackerOverlay.class',
                    'data/shatteredseals/function/progression/reset.mcfunction',
                    'data/shatteredseals/function/ngplus/start.mcfunction',
                    'data/shatteredseals/structure/smugglers_den.nbt',
                    'data/shatteredseals/structure/hollow_kings_fortress.nbt'
                )) {
                    Add-Result $(if ($names.ContainsKey($part)) { 'PASS' } else { 'FAIL' }) ('JAR contains ' + $part)
                }
            } finally {
                $zip.Dispose()
            }
        }
        $modNames = @(Get-ChildItem -LiteralPath $modsDir -File -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { $_.Name })
        foreach ($dependency in @('xaerominimap', 'xaeroworldmap', 'iris', 'sodium')) {
            $matches = @($modNames | Where-Object { $_.ToLowerInvariant().Contains($dependency) })
            Add-Result $(if ($matches.Count -gt 0) { 'PASS' } else { 'WARN' }) ('Dependency file detected: ' + $dependency)
        }
    }
    $shaderDir = Join-Path $profileDir 'shaderpacks'
    if (Test-Path $shaderDir -PathType Container) {
        $shaders = @(Get-ChildItem -LiteralPath $shaderDir -File -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match '(?i)ComplementaryReimagined|Complementary.Reimagined' })
        Add-Result $(if ($shaders.Count -gt 0) { 'PASS' } else { 'WARN' }) 'Complementary Reimagined shader ZIP present (selection still needs manual check)'
    } else {
        Add-Result 'WARN' 'No shaderpacks directory found; shaders may need manual installation'
    }

    $savesDir = Join-Path $profileDir 'saves'
    $foundAudits = 0
    if (Test-Path $savesDir -PathType Container) {
        $auditOut = Join-Path $bundleDir 'world-audits'
        New-Item -ItemType Directory -Path $auditOut -Force | Out-Null
        foreach ($world in @(Get-ChildItem -LiteralPath $savesDir -Directory -ErrorAction SilentlyContinue)) {
            $audit = Join-Path $world.FullName 'shatteredseals-tests\latest.txt'
            if (-not (Test-Path -LiteralPath $audit -PathType Leaf)) { continue }
            $foundAudits++
            $safeWorldName = $world.Name -replace '[^a-zA-Z0-9_.-]', '_'
            $destination = Join-Path $auditOut ($safeWorldName + '-latest.txt')
            Copy-Item -LiteralPath $audit -Destination $destination -Force
            $content = Get-Content -LiteralPath $audit
            $summary = @($content | Where-Object { $_ -like 'SUMMARY|*' } | Select-Object -First 1)
            if ($summary.Count -gt 0) {
                Add-Result 'PASS' ('World audit collected for ' + $world.Name + ': ' + $summary[0])
                # Forward live PASS/FAIL/WARN details into the report.
                foreach ($entry in $content) {
                    if ($entry -match '^(PASS|FAIL|WARN|NOT_TESTED)\|') {
                        $script:messages.Add('WORLD_' + $entry)
                        if ($entry.StartsWith('FAIL|')) { $script:failed++ }
                        elseif ($entry.StartsWith('WARN|')) { $script:warned++ }
                        elseif ($entry.StartsWith('NOT_TESTED|')) { $script:unverified++ }
                        else { $script:passed++ }
                    }
                }
            } else {
                Add-Result 'WARN' ('Audit file malformed for world ' + $world.Name)
            }
        }
    }
    if ($foundAudits -eq 0) {
        Add-Result 'NOT_TESTED' 'No world audit found. Enter a world, run /shatteredseals audit and re-run this collector.'
    }

    $latestLog = Join-Path $profileDir 'logs\latest.log'
    if (Test-Path -LiteralPath $latestLog -PathType Leaf) {
        $lines = @(Get-Content -LiteralPath $latestLog -Tail 5000 -ErrorAction SilentlyContinue |
            Where-Object { $_ -match '(?i)shatteredseals|shattered.seals|exception|\berror\b|fatal' } |
            Select-Object -Last 100)
        if ($lines.Count -gt 0) {
            $lines | Set-Content -LiteralPath (Join-Path $bundleDir 'filtered-log-excerpts.txt') -Encoding UTF8
        }
        Add-Result 'PASS' 'latest.log found; only relevant log lines copied'
    } else {
        Add-Result 'WARN' 'latest.log not found in the profile'
    }
    $crashesDir = Join-Path $profileDir 'crash-reports'
    if (Test-Path $crashesDir -PathType Container) {
        $crashes = @(Get-ChildItem -LiteralPath $crashesDir -File -ErrorAction SilentlyContinue)
        if ($crashes.Count -gt 0) {
            Add-Result 'WARN' ('Crash reports exist: ' + $crashes.Count + ' (not automatically copied)')
            $crashes | ForEach-Object { $_.Name } |
                Set-Content -LiteralPath (Join-Path $bundleDir 'crash-report-filenames.txt') -Encoding UTF8
        }
    }
    Add-Result 'NOT_TESTED' 'Actual dungeon layout and all six bosses need dedicated in-game playtests'
    Add-Result 'NOT_TESTED' 'Visual HUD, shader selection, controls and minimap waypoints require visual inspection'
    Add-Result 'NOT_TESTED' 'Full campaign progression and save/reload require an extended playthrough'

    $header = @(
        'THE SHATTERED SEALS - WINDOWS TEST REPORT',
        'Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'),
        'Runtime audit: /shatteredseals audit',
        'No worlds or player inventory were modified.',
        'Review filtered-log-excerpts.txt before sharing; logs can contain usernames or file paths.',
        ''
    )
    $report = Join-Path $bundleDir 'report-summary.txt'
    ($header + @($messages)) | Set-Content -LiteralPath $report -Encoding UTF8
    Compress-Archive -Path (Join-Path $bundleDir '*') -DestinationPath $zipPath -Force
    Write-Host ''
    Write-Host ('Report summary: PASS=' + $passed + ', FAIL=' + $failed + ', WARN=' + $warned + ', NOT_TESTED=' + $unverified)
    Write-Host ('Report folder: ' + $bundleDir)
    Write-Host ('Shareable ZIP: ' + $zipPath)
    Write-Host 'Review the ZIP contents before uploading. No remote connection or upload occurred.'
} catch {
    Write-Host ('The report collector encountered an error: ' + $_.Exception.Message)
    exit 1
}
