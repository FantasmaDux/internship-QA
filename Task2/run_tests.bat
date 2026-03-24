echo Moving to project root folder...
cd test-automation
if %errorlevel% neq 0 (
    echo Folder test-automation not found!
    pause
    exit /b %errorlevel%
)
echo.

echo Running tests...

call mvnw.cmd clean test
set TEST_EXIT_CODE=%errorlevel%

echo.
echo Generating Allure Report...
echo.

echo.
echo Generating static report...

call mvnw.cmd allure:report
copy "target\site\allure-maven-plugin\index.html" ".\allure-example.html"

start cmd /k mvnw.cmd allure:serve --no-transfer-progress

echo You can see results above!

pause