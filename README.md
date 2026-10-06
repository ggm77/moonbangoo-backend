# 어른문방구 - 백엔드

어른문방구 카드팩 뽑기 이벤트([프론트엔드](https://github.com/jeondowon/moonbangoo))의 API 서버입니다.

- 사장님은 카드팩(카테고리)을 만들고, 팩 안에 경품과 수량을 넣어 구성합니다.
- 손님은 팩을 고르면 그 팩에 들어있는 경품 중에서 카드 5장을 받고, 5장 중 1장을 골라 확정합니다.
- 고른 경품의 남은 수량만 1 차감되고, 모든 경품이 소진된 팩은 품절이 됩니다.
- 하루 1회 같은 참여 제한, 쿠폰 발급은 구현하지 않았습니다.

API 명세는 [docs/API.md](docs/API.md)를 참고하세요.

## 기술 스택

- Java 25, Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- MariaDB (테스트는 H2 인메모리)
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
    url: jdbc:mariadb://localhost:3306/moonbangoo
    username: root
    password: <DB 비밀번호>
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false

cors:
  allowed-origins: http://localhost:5173,https://jeondowon.github.io

springdoc:
  api-docs:
    path: /api/v3/api-docs
  swagger-ui:
    path: /api/swagger
```

## 실행 및 테스트

```bash
./gradlew bootRun   # 서버 실행 (MariaDB 필요)
./gradlew test      # 테스트 (H2 사용, src/test/resources/application-test.yaml)
```
