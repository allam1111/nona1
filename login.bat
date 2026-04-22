@echo off
echo ========================================
echo DEBUG: Firebase Login Helper
echo ========================================

:: Check drive and directory
d:
cd "d:\my web\allam ai"
if %errorlevel% neq 0 (
    echo Error: Could not change directory to d:\my web\allam ai
    pause
    exit /b
)

:: Set PATH to include Node
set PATH=C:\Users\dell\AppData\Local\stm32cube\bundles\node\20.19.4+st.1\node\bin;%PATH%

echo Node version:
node -v
if %errorlevel% neq 0 (
    echo Error: Node.js not found at the expected path.
    pause
    exit /b
)

echo Starting Firebase Login...
echo.
npx firebase login --reauth

if %errorlevel% neq 0 (
    echo.
    echo Login failed. Please check the error message above.
) else (
    echo.
    echo Success! Now close this window and say "Done" in the chat.
)

pause
