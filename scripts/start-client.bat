@echo off
setlocal enabledelayedexpansion
title AI Agent Black Box - JavaFX Desktop Client
echo ======================================================================
echo   AI AGENT BLACK BOX - JAVAFX DESKTOP FLIGHT DASHBOARD
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
    echo [INFO] Using JAVA_HOME: %JAVA_HOME%
) else (
    echo [WARN] JAVA_HOME not explicitly set; searching system PATH...
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

echo [INFO] Using Maven: %MVN_CMD%
echo [INFO] Launching JavaFX GUI Client (Connecting to http://localhost:8081)...
echo ----------------------------------------------------------------------

call "%MVN_CMD%" javafx:run -pl desktop-client
pause
