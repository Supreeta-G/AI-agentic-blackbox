# AI Agent Black Box - Spring Boot Backend Launcher (PowerShell)
$Host.UI.RawUI.WindowTitle = "AI Agent Black Box - Spring Boot Backend (Port 8081)"
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  AI AGENT BLACK BOX - SPRING BOOT FLIGHT RECORDER BACKEND" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$projectDir = Resolve-Path "$PSScriptRoot\..\AI-agentic-blackbox"
Set-Location $projectDir

# 1. Detect JAVA_HOME
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Users\Ananyaa\.jdks\openjdk-26.0.2") {
        $env:JAVA_HOME = "C:\Users\Ananyaa\.jdks\openjdk-26.0.2"
    } elseif (Test-Path "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr") {
        $env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr"
    }
}

if ($env:JAVA_HOME) {
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "[INFO] Using JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
}

# 2. Detect Maven
$mvnCmd = "mvn"
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd") {
        $mvnCmd = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
    } elseif ($env:M2_HOME -and (Test-Path "$env:M2_HOME\bin\mvn.cmd")) {
        $mvnCmd = "$env:M2_HOME\bin\mvn.cmd"
    }
}

Write-Host "[INFO] Using Maven: $mvnCmd" -ForegroundColor Green
Write-Host "[INFO] Starting Spring Boot on http://localhost:8081..." -ForegroundColor Yellow
Write-Host "----------------------------------------------------------------------"

& $mvnCmd spring-boot:run -pl backend
