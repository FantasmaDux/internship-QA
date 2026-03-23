@echo off
setlocal enabledelayedexpansion

echo Moving to project root folder...
cd test-automation
if %errorlevel% neq 0 (
    echo Folder test-automation not found!
    pause
    exit /b %errorlevel%
)
echo.

echo Running tests...

mvnw.cmd test 

echo You can see results above!
pause