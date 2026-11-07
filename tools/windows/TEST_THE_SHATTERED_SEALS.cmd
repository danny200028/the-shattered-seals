@echo off
setlocal
title The Shattered Seals - Test Report Collector
echo.
echo THE SHATTERED SEALS - AUTOMATED TEST REPORT
echo.
echo Start Minecraft first, enter a test world and type:
echo    /shatteredseals audit
echo Then close Minecraft and run this collector.
echo.
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\ShatteredSeals-TestReport.ps1"
echo.
pause
