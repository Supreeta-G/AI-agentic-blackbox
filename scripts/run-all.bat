@echo off
setlocal enabledelayedexpansion
title AI Agent Black Box - Master Launcher
echo ======================================================================
echo   AI AGENT BLACK BOX - FULL SYSTEM MASTER LAUNCHER
echo   Spring Boot Backend (Port 8081) + JavaFX Flight Recorder UI
echo ======================================================================

cd /d "%~dp0\.."

:: 1. Detect JAVA_HOME
if "%JAVA_HOME%"=="" (
    if exist "C:\Users\Ananyaa\.jdks\openjdk-26.0.2" (
        set "JAVA_HOME=C:\Users\Ananyaa\.jdks\openjdk-26.0.2"
    ) else if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr" (
        set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr"
    )
)

if not "%JAVA_HOME%"=="" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [INFO] Detected JAVA_HOME: %JAVA_HOME%
)

:: 2. Detect Maven
set "MVN_CMD=mvn"
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
    ) else if exist "%M2_HOME%\bin\mvn.cmd" (
        set "MVN_CMD=%M2_HOME%\bin\mvn.cmd"
    )
)

echo [INFO] Detected Maven: %MVN_CMD%

echo.
echo [STEP 1/2] Starting Spring Boot Flight Recorder Backend on http://localhost:8081...
start "AI Agent Black Box - Backend" cmd /k "call "%MVN_CMD%" spring-boot:run -pl backend"

echo.
echo [STEP 2/2] Waiting for Backend to initialize...
timeout /t 8 /nobreak >nul

echo [INFO] Starting JavaFX Desktop Flight Recorder UI...
call "%MVN_CMD%" javafx:run -pl desktop-client

echo.
echo System session completed.
pause

