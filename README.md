# 어른문방구 - 백엔드

어른문방구 카드팩 뽑기 이벤트([프론트엔드](https://github.com/jeondowon/moonbangoo))의 API 서버입니다.

- 사장님은 카드팩(카테고리)을 만들고, 팩 안에 경품과 수량을 넣어 구성합니다.
- 손님은 팩을 고르면 그 팩에 들어있는 경품 중에서 카드 5장을 받고, 5장 중 1장을 골라 확정합니다.
- 고른 경품의 남은 수량만 1 차감되고, 모든 경품이 소진된 팩은 품절이 됩니다.
- 하루 1회 같은 참여 제한, 쿠폰 발급은 구현하지 않았습니다.

API 명세는 [docs/API.md](docs/API.md)를 참고하세요.

## 기술 스택

- Java 25, Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- SQLite (파일 DB, 테스트는 인메모리)
- springdoc-openapi (Swagger UI: `/api/swagger`)

## 뽑기 규칙

1. 손님이 고른 팩 안에서 남은 수량(`remaining`)이 1 이상인 경품만 후보가 됩니다. 비활성 팩은 뽑을 수 없습니다.
2. 남은 수량을 가중치로 사용해 한 장씩 뽑습니다. 남은 경품 1개가 추첨권 1장이라고 보면 되고, 수량이 줄면 뽑힐 확률도 자동으로 줄어듭니다.
3. 5장은 한 장씩 따로 뽑습니다(복원 추출). 뽑힌 경품을 후보에서 빼지 않으므로 같은 경품이 여러 장 나올 수 있습니다. 손님은 한 장만 가져가고 한 장만 차감하므로 남은 수량보다 많이 보여도 됩니다.
4. 결과는 등급 오름차순(C → R → SR → UR)으로 정렬되어, 프론트가 받은 순서대로 넘기면 레어 카드가 뒤쪽에 나옵니다.
5. 뽑기만으로는 수량이 차감되지 않습니다. 손님이 "이 상품으로 결정"을 누르는 확정 API를 호출해야 고른 경품 1개만 차감됩니다. 연출 도중 새로고침·이탈해도 차감되지 않습니다.
6. 후보가 하나도 없으면 `409 PACK_SOLD_OUT`을 반환합니다. 모든 팩이 품절이면 팩 목록 API의 `available`이 전부 `false`이므로 "이벤트 종료"를 보여주면 됩니다.
7. 경품 수량은 팩끼리 공유하지 않습니다. 같은 이름의 경품이 다른 팩에 있어도 각자 수량을 가집니다.

## 어드민 페이지

사장님 기기 브라우저에서 `http://서버주소:포트/admin` 으로 접속합니다(예: `http://192.168.0.10:53838/admin`). 화면과 API가 같은 서버에서 나가므로 주소가 하나이고, 별도 설치나 외부 인터넷 연결이 필요 없습니다. 폰, 태블릿, PC 모두 사용할 수 있습니다.

- **경품 탭**: 전체 남은 수량과 등급별 현황, 팩별 경품 목록. 수량은 `−`/`＋` 버튼이나 숫자를 직접 입력해서 바로 바꿉니다. 부족(2개 이하)·품절 경품은 색과 글자로 표시되고 "부족/품절만 보기"로 걸러 볼 수 있습니다. 경품은 이름을 누르면 수정·삭제·팩 이동을 할 수 있고, 추가 화면의 "추가하고 계속"으로 여러 개를 연달아 등록할 수 있습니다.
- **팩**: 처음에는 팩이 없으므로 "기본 팩 7개 한 번에 만들기"로 시작할 수 있습니다. 팩마다 "손님에게 보임" 스위치로 준비 중인 팩을 숨길 수 있습니다.
- **당첨 기록 탭**: 손님이 가져간 경품 목록. 잘못 눌렀거나 경품을 아직 주지 않았다면 "되돌리기"로 수량을 복원합니다.
- **설정 탭**: 이벤트 초기화(확인 문구 입력 필요), 백업 안내.
- 손님이 뽑는 동안에도 수량이 바뀌므로 화면은 8초마다 자동으로 갱신됩니다(입력 중이거나 창이 열려 있을 때는 건너뜀). 화면 파일은 `src/main/resources/admin/index.html` 하나입니다.

## 운영 방식

어드민 페이지와 어드민 API(`/api/v1/admin/**`)는 비밀번호로 로그인해야 쓸 수 있습니다. 비밀번호는 `application.yaml`의 `admin.password`에 평문으로 있으니 처음에 꼭 바꾸세요. 손님이 쓰는 게임과 뽑기 API는 로그인이 없습니다. 서버는 가게 내부망에서만 접속할 수 있게 운영하고, 게임은 가게 TV, 어드민은 사장님 기기에서 같은 서버에 접속하는 것을 전제로 합니다. 로그인은 http 평문이므로 서버 포트를 외부에 공개하지 마세요(포트포워딩 금지). 이벤트 초기화처럼 되돌릴 수 없는 API는 `{"confirm": true}`를 요구합니다.

## 설정

설정은 `src/main/resources/application.yaml`에 있고 git에 포함됩니다. 어드민 비밀번호(`admin.password`)는 예외로 이 파일에 평문으로 둡니다. 그 밖의 API 키 같은 민감한 값은 환경변수나 `application-dev.yaml`, `application-prod.yaml`(git에서 제외됨)로 분리하세요.

- `admin.password`: 어드민 페이지 비밀번호입니다. 비어 있으면 서버가 시작되지 않습니다. 한 번 로그인하면 12시간(`server.servlet.session.timeout`) 동안 유지되고, 서버를 다시 켜면 다시 입력해야 합니다.
- `spring.datasource`: 서버를 실행한 폴더의 SQLite 파일 `moonbangoo.db`를 사용합니다.
- `server.port`: 기본 8080입니다.
- `upload.dir`: 업로드 API로 올린 사진을 저장하는 폴더입니다(기본 `./uploads`, 서버를 실행한 폴더 기준). DB 파일과 마찬가지로 git에 올라가지 않으니 백업할 때 `moonbangoo.db`와 함께 복사하세요. 사진은 5MB까지, png/jpg/gif/webp만 올릴 수 있습니다.
- `cors.allowed-origins`: 허용할 프론트엔드 주소 목록(쉼표로 구분)입니다.
- `springdoc`: API 문서 경로(`/api/v3/api-docs`)와 Swagger UI(`/api/swagger`)입니다.

## 데이터베이스

- 데이터는 서버를 실행한 폴더의 `moonbangoo.db` 파일 하나에 저장됩니다(`-wal`, `-shm` 파일은 SQLite가 같이 만드는 임시 파일). 백업은 서버를 끈 뒤 이 파일을 복사하면 됩니다. DB 파일은 git에 올라가지 않습니다.
- SQLite는 동시에 쓸 수 없어서 DB 연결을 1개만 쓰고(`maximum-pool-size: 1`) 요청을 순서대로 처리합니다. 가게 규모에서는 충분하고, 여러 손님이 동시에 같은 경품을 확정해도 남은 수량만큼만 성공합니다.
- 테이블은 `src/main/resources/schema.sql`이 서버를 시작할 때 만듭니다(이미 있으면 건드리지 않음). Hibernate의 SQLite dialect가 자동 증가 id 컬럼을 올바르게 만들지 못해서 `ddl-auto`를 쓰지 않고 직접 관리합니다. **엔티티 컬럼을 바꾸면 `schema.sql`도 같이 고치고**, 이미 만들어진 DB 파일에는 `ALTER TABLE`을 직접 실행하거나 DB 파일을 지우고 다시 만들어야 합니다.
- 이전에 MariaDB를 쓰던 데이터는 옮겨지지 않습니다.

## 실행 파일 만들기 (Windows exe, 맥)

서버 jar를 실행해 주는 작은 실행기(`launcher/launcher.py`)를 실행 파일로 묶어 두면, 가게 PC에서 더블클릭만으로 서버를 켤 수 있습니다. 실행기는 같은 폴더의 `moonbangoo.jar`를 실행하고, 서버가 준비되면 접속 주소를 보여주며 손님 화면을 브라우저로 엽니다. 창을 닫거나 Ctrl+C를 누르면 서버도 같이 종료됩니다.

PyInstaller는 빌드한 OS와 CPU용 파일만 만들 수 있어서, 쓸 PC의 OS에서 각각 빌드해야 합니다. 둘 다 Python 3.8+와 JDK 25가 필요합니다.

| OS | 빌드 | 결과물 |
| --- | --- | --- |
| Windows | `launcher\build.bat` | `launcher\release\moonbangoo.exe` + `moonbangoo.jar` |
| 맥 | `./launcher/build.sh` | `launcher/release-mac/moonbangoo` + `moonbangoo.jar` |

결과물의 실행 파일과 `moonbangoo.jar`를 **같은 폴더**에 두고 실행하세요.

- DB(`moonbangoo.db`)와 업로드 사진(`uploads`)은 실행 파일이 있는 폴더에 생깁니다. 백업도 이 폴더를 복사하면 됩니다.
- Java는 `jre` 폴더(`jre\bin\java.exe`, 맥은 `jre/bin/java`) → `JAVA_HOME` → `PATH` 순서로 찾습니다. Java 25가 없는 PC라면 JRE를 `jre` 폴더로 같이 넣어주세요.
- 포트를 바꾸려면 `moonbangoo --server.port=9000`처럼 실행합니다. 브라우저 자동 실행을 끄려면 `--no-browser`를 붙입니다.
- 맥 실행 파일은 만든 맥과 같은 CPU(Apple Silicon/Intel)에서만 실행됩니다. 다른 맥으로 옮겼을 때 "확인되지 않은 개발자" 경고로 막히면 `xattr -cr 폴더경로`를 실행하세요.

## 실행 및 테스트

```bash
./gradlew bootRun   # 서버 실행 (DB 파일이 없으면 자동으로 만들어짐)
./gradlew test      # 테스트 (SQLite 인메모리, src/test/resources/application-test.yaml)
```
