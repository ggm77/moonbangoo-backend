"""
어른문방구 서버 실행기

exe와 같은 폴더에 있는 moonbangoo.jar 를 실행하고, 접속 주소를 안내한다.
DB 파일(moonbangoo.db)과 업로드 사진(uploads)은 exe가 있는 폴더에 만들어진다.
이 창을 닫거나 Ctrl+C 를 누르면 서버도 같이 종료된다.

Java 는 아래 순서로 찾는다.
  1. exe 옆의 jre 폴더 (jre/bin/java.exe)
  2. JAVA_HOME 환경변수
  3. PATH 에 등록된 java
"""
import os
import shutil
import signal
import socket
import subprocess
import sys
import threading
import time
import urllib.request
import webbrowser

DEFAULT_PORT = 8080
FRONT_PATH = "/moonbangoo/"
ADMIN_PATH = "/admin"


def base_dir():
    # PyInstaller 로 묶었을 때는 exe 위치, 그냥 실행할 때는 이 파일 위치
    if getattr(sys, "frozen", False):
        return os.path.dirname(os.path.abspath(sys.executable))
    return os.path.dirname(os.path.abspath(__file__))


def find_jar(folder):
    jars = sorted(f for f in os.listdir(folder) if f.startswith("moonbangoo") and f.endswith(".jar"))
    if "moonbangoo.jar" in jars:
        return os.path.join(folder, "moonbangoo.jar")
    return os.path.join(folder, jars[0]) if jars else None


def find_java(folder):
    exe = "java.exe" if os.name == "nt" else "java"

    bundled = os.path.join(folder, "jre", "bin", exe)
    if os.path.isfile(bundled):
        return bundled

    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = os.path.join(java_home, "bin", exe)
        if os.path.isfile(candidate):
            return candidate

    return shutil.which("java")


def local_ips():
    # 같은 와이파이의 다른 기기(사장님 폰 등)에서 접속할 때 쓸 주소
    ips = set()
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
            s.connect(("10.255.255.255", 1))  # 실제로 패킷을 보내지는 않음
            ips.add(s.getsockname()[0])
    except OSError:
        pass
    try:
        for info in socket.getaddrinfo(socket.gethostname(), None, socket.AF_INET):
            ips.add(info[4][0])
    except OSError:
        pass
    return sorted(ip for ip in ips if not ip.startswith("127."))


def parse_port(args):
    for arg in args:
        if arg.startswith("--server.port="):
            try:
                return int(arg.split("=", 1)[1])
            except ValueError:
                pass
    return DEFAULT_PORT


def wait_until_ready(port, process, timeout=90):
    url = "http://127.0.0.1:%d/ping" % port
    deadline = time.time() + timeout
    while time.time() < deadline and process.poll() is None:
        try:
            with urllib.request.urlopen(url, timeout=2) as res:
                if res.status == 200:
                    return True
        except OSError:
            time.sleep(0.5)
    return False


def stop(process):
    if process.poll() is not None:
        return
    process.terminate()
    try:
        process.wait(timeout=10)
    except subprocess.TimeoutExpired:
        process.kill()


def pause_and_exit(code):
    # 더블클릭으로 실행했을 때 오류 메시지를 읽기도 전에 창이 닫히지 않도록
    try:
        input("\n엔터를 누르면 닫힙니다.")
    except EOFError:
        pass
    sys.exit(code)


def main():
    args = [a for a in sys.argv[1:] if a != "--no-browser"]
    open_browser = "--no-browser" not in sys.argv[1:]

    folder = base_dir()

    jar = find_jar(folder)
    if not jar:
        print("[오류] 같은 폴더에서 moonbangoo.jar 를 찾지 못했습니다.")
        print("       폴더: " + folder)
        pause_and_exit(1)

    java = find_java(folder)
    if not java:
        print("[오류] Java 를 찾지 못했습니다. Java 25 이상을 설치하거나")
        print("       이 폴더에 jre 폴더(jre/bin/java.exe)를 넣어주세요.")
        pause_and_exit(1)

    port = parse_port(args)
    print("어른문방구 서버를 시작합니다...")
    print("  java: " + java)
    print("  jar : " + jar)
    print("  데이터 폴더: " + folder)
    print()

    # cwd 를 exe 폴더로 맞춰야 moonbangoo.db 와 uploads 가 이 폴더에 생긴다
    try:
        process = subprocess.Popen([java, "-jar", jar] + args, cwd=folder)
    except OSError as e:
        print("[오류] 서버를 실행하지 못했습니다: %s" % e)
        pause_and_exit(1)

    # 종료 신호(SIGTERM, 터미널 닫힘, Windows 의 Ctrl+Break)도 Ctrl+C 와 똑같이 정리하도록 처리
    def on_signal(signum, frame):
        raise KeyboardInterrupt

    for name in ("SIGTERM", "SIGHUP", "SIGBREAK"):
        if hasattr(signal, name):
            signal.signal(getattr(signal, name), on_signal)

    try:
        if wait_until_ready(port, process):
            print()
            print("=" * 56)
            print(" 서버가 준비되었습니다.")
            print()
            print(" 손님 화면 (가게 TV)  http://localhost:%d%s" % (port, FRONT_PATH))
            print(" 어드민 (사장님 기기)  http://localhost:%d%s" % (port, ADMIN_PATH))
            for ip in local_ips():
                print(" 같은 와이파이의 다른 기기  http://%s:%d%s" % (ip, port, ADMIN_PATH))
            print()
            print(" 종료하려면 이 창을 닫거나 Ctrl+C 를 누르세요.")
            print("=" * 56)
            print()
            if open_browser:
                threading.Thread(
                    target=webbrowser.open,
                    args=("http://localhost:%d%s" % (port, FRONT_PATH),),
                    daemon=True,
                ).start()

        code = process.wait()
        if code not in (0, 130, 143):  # 130, 143 은 Ctrl+C / 종료 신호로 끈 경우
            print("\n[오류] 서버가 종료되었습니다. (종료 코드 %s)" % code)
            print("       위쪽 로그에서 원인을 확인하세요. 포트 %d 를 다른 프로그램이 쓰고 있을 수 있습니다." % port)
            pause_and_exit(code)
    except KeyboardInterrupt:
        print("\n서버를 종료하는 중...")
    finally:
        stop(process)


if __name__ == "__main__":
    main()
