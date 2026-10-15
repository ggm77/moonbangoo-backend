@echo off
rem Builds the server jar and packs launcher.py into an exe. Output: launcher\release
rem Run this on Windows to get a Windows exe. Requires Python 3.8+ and JDK 25.
setlocal
cd /d "%~dp0"

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
python -m pip install --quiet pyinstaller || goto :fail
python -m PyInstaller --onefile --console --noconfirm --name moonbangoo ^
    --distpath release --workpath build\pyi --specpath build\pyi launcher.py || goto :fail

echo.
echo Done: %cd%\release
echo   Keep moonbangoo.exe and moonbangoo.jar in the same folder and run moonbangoo.exe.
echo   On a PC without Java 25, also put a "jre" folder (jre\bin\java.exe) next to them.
exit /b 0

:fail
echo.
echo [FAILED] See the messages above.
exit /b 1
