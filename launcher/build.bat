@echo off
rem Builds the server jar and packs launcher.py into an exe. Output: launcher\release
rem Run this on Windows to get a Windows exe. Requires Python 3.8+ and JDK 25.
setlocal
cd /d "%~dp0"

echo [0/3] Checking Python
set "PY="
py -3 --version >nul 2>&1 && set "PY=py -3"
if not defined PY (
    python --version >nul 2>&1 && set "PY=python"
)
if not defined PY (
    echo [ERROR] Python was not found. Install Python 3.8+ from python.org and
    echo         check "Add python.exe to PATH" in the installer.
    echo         ^(The "python" shortcut from the Microsoft Store does not work.^)
    goto :fail
)
%PY% --version

echo [1/3] Building server jar
pushd ..
call gradlew.bat bootJar -x test || goto :fail
popd

echo [2/3] Preparing release folder
if exist release rmdir /s /q release
mkdir release || goto :fail
for %%f in (..\build\libs\moonbangoo-*.jar) do (
    echo %%f | findstr /i "plain" >nul || copy /y "%%f" release\moonbangoo.jar >nul
)
if not exist release\moonbangoo.jar (
    echo [ERROR] jar not found in build\libs
    goto :fail
)

echo [3/3] Building exe
%PY% -m pip install pyinstaller || goto :fail
%PY% -m PyInstaller --onefile --console --noconfirm --name moonbangoo ^
    --distpath release --workpath build\pyi --specpath build\pyi launcher.py || goto :fail
if not exist release\moonbangoo.exe (
    echo [ERROR] PyInstaller finished but release\moonbangoo.exe was not created.
    goto :fail
)

echo.
echo Done: %cd%\release
echo   Keep moonbangoo.exe and moonbangoo.jar in the same folder and run moonbangoo.exe.
echo   On a PC without Java 25, also put a "jre" folder ^(jre\bin\java.exe^) next to them.
call :maybe_pause
exit /b 0

:fail
echo.
echo [FAILED] See the messages above.
call :maybe_pause
exit /b 1

:maybe_pause
rem keep the window open when the bat was double-clicked
echo %cmdcmdline% | find /i "/c" >nul && pause
exit /b 0
