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
3. 후보가 5종 이상이면 뽑힌 경품을 후보에서 빼고 다음 장을 뽑아(비복원 추출) 5장이 모두 서로 다릅니다.
4. 후보가 5종 미만이면 모든 종류를 한 장씩 넣고, 남은 자리는 남은 수량에 비례해 중복으로 채웁니다. 손님은 한 장만 가져가고 한 장만 차감하므로 남은 수량보다 많이 보여도 됩니다.
5. 결과는 등급 오름차순(C → R → SR → UR)으로 정렬되어, 프론트가 받은 순서대로 넘기면 레어 카드가 뒤쪽에 나옵니다.
6. 뽑기만으로는 수량이 차감되지 않습니다. 손님이 "이 상품으로 결정"을 누르는 확정 API를 호출해야 고른 경품 1개만 차감됩니다. 연출 도중 새로고침·이탈해도 차감되지 않습니다.
7. 후보가 하나도 없으면 `409 PACK_SOLD_OUT`을 반환합니다. 모든 팩이 품절이면 팩 목록 API의 `available`이 전부 `false`이므로 "이벤트 종료"를 보여주면 됩니다.
8. 경품 수량은 팩끼리 공유하지 않습니다. 같은 이름의 경품이 다른 팩에 있어도 각자 수량을 가집니다.

## 운영 방식

로그인과 인증은 없습니다. 서버는 가게 내부망에서만 접속할 수 있게 운영하고, 게임은 가게 TV, 어드민은 사장님 기기에서 같은 서버에 접속하는 것을 전제로 합니다. 어드민 API(`/api/v1/admin/**`)도 인증 없이 호출할 수 있으므로 서버 포트를 외부에 공개하지 마세요(포트포워딩 금지). 이벤트 초기화처럼 되돌릴 수 없는 API는 `{"confirm": true}`를 요구합니다.

## 설정

`src/main/resources/application.yaml`은 git에 올리지 않습니다. 아래 형식으로 작성하세요.

```yaml
spring:
  application:
    name: moonbangoo
  datasource:
    url: jdbc:sqlite:./moonbangoo.db?foreign_keys=on&busy_timeout=5000&journal_mode=WAL
    driver-class-name: org.sqlite.JDBC
    hikari:
      maximum-pool-size: 1
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: none
    open-in-view: false
  sql:
    init:
      mode: always

cors:
  allowed-origins: http://localhost:5173,https://jeondowon.github.io

springdoc:
  api-docs:
    path: /api/v3/api-docs
  swagger-ui:
    path: /api/swagger
```

## 데이터베이스

- 데이터는 서버를 실행한 폴더의 `moonbangoo.db` 파일 하나에 저장됩니다(`-wal`, `-shm` 파일은 SQLite가 같이 만드는 임시 파일). 백업은 서버를 끈 뒤 이 파일을 복사하면 됩니다. DB 파일은 git에 올라가지 않습니다.
- SQLite는 동시에 쓸 수 없어서 DB 연결을 1개만 쓰고(`maximum-pool-size: 1`) 요청을 순서대로 처리합니다. 가게 규모에서는 충분하고, 여러 손님이 동시에 같은 경품을 확정해도 남은 수량만큼만 성공합니다.
- 테이블은 `src/main/resources/schema.sql`이 서버를 시작할 때 만듭니다(이미 있으면 건드리지 않음). Hibernate의 SQLite dialect가 자동 증가 id 컬럼을 올바르게 만들지 못해서 `ddl-auto`를 쓰지 않고 직접 관리합니다. **엔티티 컬럼을 바꾸면 `schema.sql`도 같이 고치고**, 이미 만들어진 DB 파일에는 `ALTER TABLE`을 직접 실행하거나 DB 파일을 지우고 다시 만들어야 합니다.
- 이전에 MariaDB를 쓰던 데이터는 옮겨지지 않습니다.

## 실행 및 테스트

```bash
./gradlew bootRun   # 서버 실행 (DB 파일이 없으면 자동으로 만들어짐)
./gradlew test      # 테스트 (SQLite 인메모리, src/test/resources/application-test.yaml)
```
