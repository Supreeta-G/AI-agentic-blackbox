@echo off
echo ========================================================
echo Starting AI Agent Black Box - JavaFX Desktop Client
echo ========================================================
cd /d "%~dp0\.."

set MVN_CMD=mvn
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    if exist "C:\Users\SWETHA\tools\maven\apache-maven-3.9.9\bin\mvn.cmd" (
        set "MVN_CMD=C:\Users\SWETHA\tools\maven\apache-maven-3.9.9\bin\mvn.cmd"
    )
)

call "%MVN_CMD%" javafx:run -pl desktop-client
pause
