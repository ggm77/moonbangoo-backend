# 어른문방구 - 백엔드

어른문방구 카드팩 뽑기 이벤트([프론트엔드](https://github.com/jeondowon/moonbangoo))의 API 서버입니다.

- 사장님은 경품을 등록하고 경품별 확률과 재고를 수정할 수 있습니다.
- 손님은 카드 뽑기를 요청하면 서로 다른 경품 카드 5장을 받습니다.
- 하루 1회 같은 참여 제한, 경품 선택·쿠폰 발급은 아직 구현하지 않았습니다.

API 명세는 [docs/API.md](docs/API.md)를 참고하세요.

## 기술 스택

- Java 25, Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- MariaDB (테스트는 H2 인메모리)
- springdoc-openapi (Swagger UI: `/api/swagger`)

## 뽑기 규칙

1. 확률이 0보다 크고 재고가 남은(`stock`이 `null`이거나 1 이상) 경품만 후보가 됩니다.
2. 경품의 확률을 가중치로 사용해 한 장씩 뽑고, 뽑힌 경품은 후보에서 빼고 다음 장을 뽑습니다(비복원 추출). 그래서 5장 모두 서로 다른 경품입니다.
3. 확률 합이 1이 아니어도 후보들의 확률 합 기준 비율로 뽑힙니다. 사장님 화면용으로 경품 목록 API가 확률 합(`totalProbability`)을 같이 줍니다.
4. 결과는 등급 오름차순(C → R → SR → UR)으로 정렬되어, 프론트가 받은 순서대로 넘기면 레어 카드가 뒤쪽에 나옵니다.
5. 후보 경품이 5개보다 적으면 `409 NOT_ENOUGH_PRIZE`를 반환합니다.

※ 비복원 추출이므로 "5장 중에 해당 경품이 포함될 확률"은 설정한 확률과 다릅니다. 설정한 확률은 한 장을 뽑을 때 그 경품이 나올 비율입니다.

## 운영 방식

로그인과 인증은 없습니다. 서버는 사장님 개인 컴퓨터에서만 실행하고, 웹사이트는 가게 안에서만 이용하는 것을 전제로 합니다. 경품 관리 API(`/api/v1/admin/**`)도 인증 없이 호출할 수 있으므로 서버 포트를 외부에 공개하지 마세요.

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
