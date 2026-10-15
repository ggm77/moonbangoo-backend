#!/usr/bin/env bash
# 서버 jar를 만들고 launcher.py 를 맥(리눅스)용 실행 파일로 묶는다. 결과물: launcher/release-mac
# 만든 맥과 같은 CPU(Apple Silicon / Intel)에서만 실행된다. Python 3.8+ 와 JDK 25 필요.
set -euo pipefail

cd "$(dirname "$0")"
OUT=release-mac

# 다른 버전 Python 의 패키지가 PYTHONPATH 로 섞여 들어와 빌드가 깨지는 것을 막는다
unset PYTHONPATH

echo "[0/3] Python 확인"
command -v python3 >/dev/null || { echo "[오류] python3 를 찾지 못했습니다."; exit 1; }
python3 --version

echo "[1/3] 서버 jar 빌드"
(cd .. && ./gradlew bootJar -x test)

echo "[2/3] $OUT 폴더 준비"
JAR=$(ls ../build/libs/moonbangoo-*.jar | grep -v plain | head -n 1)
[ -n "$JAR" ] || { echo "[오류] build/libs 에서 jar 를 찾지 못했습니다."; exit 1; }
rm -rf "$OUT"
mkdir -p "$OUT"
cp "$JAR" "$OUT/moonbangoo.jar"

echo "[3/3] 실행 파일 생성"
# 시스템 Python 을 건드리지 않도록 빌드용 가상환경을 따로 쓴다 (build/ 는 git 에서 제외됨)
python3 -m venv build/venv
build/venv/bin/python -m pip install --quiet pyinstaller
build/venv/bin/python -m PyInstaller --onefile --console --noconfirm --name moonbangoo \
    --distpath "$OUT" --workpath build/pyi --specpath build/pyi launcher.py

[ -x "$OUT/moonbangoo" ] || { echo "[오류] $OUT/moonbangoo 가 만들어지지 않았습니다."; exit 1; }

echo
echo "완료: $(pwd)/$OUT"
echo "  moonbangoo 와 moonbangoo.jar 를 같은 폴더에 두고 ./moonbangoo 로 실행하세요."
echo "  다른 맥으로 옮겼는데 '확인되지 않은 개발자' 경고로 막히면: xattr -cr 폴더경로"
echo "  Java 25 가 없는 맥에서는 jre 폴더(jre/bin/java)를 같이 넣어주세요."
