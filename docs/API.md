# 어른문방구 API 명세서

- 작성일: 2026-09-26
- 범위: 경품 관리(어드민), 카드 뽑기, 상태 확인 API

## 1. 공통 규칙

| 항목 | 규칙 |
| --- | --- |
| 서비스 API 접두사 | `/api/v1` |
| 상태 확인 경로 | `/ping`, `/ready` — `/api/v1`을 붙이지 않는다. |
| 요청 본문 | `Content-Type: application/json` |
| 성공 응답 | 응답 객체를 바로 반환한다. 공통 래퍼는 없다. |
| 생성 성공 | 경품 등록, 카드 뽑기도 `200 OK`를 반환한다. |
| 본문 없는 성공 | `204 No Content` |
| ID | JSON 정수, 서버 타입은 `Long` |
| 확률 | JSON 숫자, `0` ~ `1`, 소수점 6자리까지 (예: `0.05` = 5%) |
| 일시 | ISO 8601 UTC 문자열 (예: `2026-09-26T06:00:00Z`) |

### 인증

인증은 없다. 서버는 사장님 컴퓨터에서만 실행하고 가게 안에서만 접속하는 것을 전제로 하며, 모든 API를 인증 없이 호출할 수 있다.

### 에러 응답

```json
{
  "timestamp": "2026-09-26T06:00:00Z",
  "httpStatus": "BAD_REQUEST",
  "code": "PRIZE_NOT_EXIST",
  "message": "경품이 존재하지 않습니다."
}
```

| 코드 | HTTP | 설명 |
| --- | --- | --- |
| `INVALID_REQUEST` | 400 | 필수 값 누락, 길이·범위 위반 |
| `INVALID_ENUM_VALUE` | 400 | 없는 등급 값 |
| `PRIZE_NOT_EXIST` | 400 | 경품 없음 |
| `NOT_ENOUGH_PRIZE` | 409 | 뽑을 수 있는 경품이 5개 미만 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 |
| `SERVICE_UNAVAILABLE` | 503 | DB 연결 실패 (`/ready`) |

### 등급

| 값 | 이름 |
| --- | --- |
| `C` | 일반 |
| `R` | 레어 |
| `SR` | 슈퍼레어 |
| `UR` | 최고등급 |

## 2. API 목록

| 메서드 | 경로 | 기능 | 성공 |
| --- | --- | --- | --- |
| POST | `/api/v1/draw` | 카드 뽑기 (5장) | 200 |
| POST | `/api/v1/admin/prize` | 경품 등록 | 200 |
| GET | `/api/v1/admin/prizes` | 경품 목록 + 확률 합 | 200 |
| GET | `/api/v1/admin/prize/{id}` | 경품 조회 | 200 |
| PATCH | `/api/v1/admin/prize/{id}` | 경품 정보 수정 | 200 |
| PATCH | `/api/v1/admin/prize/{id}/probability` | 경품 확률 수정 | 200 |
| PATCH | `/api/v1/admin/prize/{id}/stock` | 경품 재고 수정 | 200 |
| DELETE | `/api/v1/admin/prize/{id}` | 경품 삭제 | 204 |
| GET | `/ping` | 서버 상태 확인 | 200 |
| GET | `/ready` | DB 연결 상태 확인 | 200 |

## 3. 카드 뽑기

### POST /api/v1/draw

요청 본문 없음. 확률이 0보다 크고 재고가 남은 경품 중 서로 다른 경품 5개를 확률에 따라 뽑는다. 결과는 등급 오름차순(C → UR)이며 이 순서대로 카드를 넘기면 된다. 참여 횟수 제한은 아직 없다.

응답:

```json
{
  "cards": [
    {
      "id": 1,
      "name": "5% 할인권",
      "rarity": "C",
      "category": "coupon",
      "image": null,
      "description": "매장 전 품목 5% 할인\n다른 할인과 중복 적용 불가",
      "condition": "1만원 이상 구매 시 사용"
    }
  ]
}
```

`cards`는 항상 5개다(위 예시는 1개만 표시). `image`, `description`, `condition`은 `null`일 수 있다. 확률과 재고는 응답에 포함하지 않는다.

에러: 뽑을 수 있는 경품이 5개 미만이면 `409 NOT_ENOUGH_PRIZE`.

## 4. 경품 관리

### 경품 응답 형식

경품 등록·조회·수정 API는 모두 아래 형식을 반환한다.

```json
{
  "id": 1,
  "name": "10% 할인권",
  "rarity": "R",
  "category": "coupon",
  "image": null,
  "description": "매장 전 품목 10% 할인",
  "condition": "2만원 이상 구매 시 사용",
  "probability": 0.25,
  "stock": null,
  "createdAt": "2026-09-26T06:00:00Z",
  "updatedAt": "2026-09-26T06:00:00Z"
}
```

`stock`이 `null`이면 무제한, `0`이면 뽑히지 않는다. `probability`가 `0`이어도 뽑히지 않는다.

### POST /api/v1/admin/prize

| 필드 | 타입 | 필수 | 조건 |
| --- | --- | --- | --- |
| `name` | string | 예 | 최대 50자 |
| `rarity` | string | 예 | `C`, `R`, `SR`, `UR` |
| `category` | string | 예 | 최대 30자 (예: `coupon`, `sticker`, `cafe`, `pack`) |
| `image` | string | 아니오 | 이미지 URL, 최대 2048자 |
| `description` | string | 아니오 | 최대 500자 |
| `condition` | string | 아니오 | 교환 조건 문구, 최대 200자 |
| `probability` | number | 예 | 0 ~ 1, 소수점 6자리까지 |
| `stock` | integer | 아니오 | 0 이상, 생략하거나 `null`이면 무제한 |

```json
{
  "name": "부스터팩 1팩",
  "rarity": "UR",
  "category": "pack",
  "description": "인기 카드 부스터팩 1팩",
  "condition": "현장 즉시 증정",
  "probability": 0.05,
  "stock": 10
}
```

### GET /api/v1/admin/prizes

등록 순서대로 전체 경품과 확률 합을 반환한다.

```json
{
  "prizes": [ { "...": "경품 응답 형식" } ],
  "totalProbability": 1.000000
}
```

### GET /api/v1/admin/prize/{id}

경품 하나를 반환한다.

### PATCH /api/v1/admin/prize/{id}

`name`, `rarity`, `category`, `image`, `description`, `condition` 중 바꿀 값만 보낸다. 조건은 등록과 같다.

- 보내지 않은 필드와 `null`은 변경하지 않는다.
- `image`, `description`, `condition`에 빈 문자열(`""`)을 보내면 값이 지워진다.
- 확률과 재고는 아래 전용 API로 수정한다.

```json
{ "name": "15% 할인권", "description": "" }
```

### PATCH /api/v1/admin/prize/{id}/probability

```json
{ "probability": 0.1 }
```

`probability`는 필수, 0 ~ 1, 소수점 6자리까지.

### PATCH /api/v1/admin/prize/{id}/stock

```json
{ "stock": 30 }
```

`stock`은 0 이상. `null`을 보내면 무제한으로 바뀐다.

### DELETE /api/v1/admin/prize/{id}

`204 No Content`.

## 5. 상태 확인

| 경로 | 성공 응답 |
| --- | --- |
| `GET /ping` | `pong` |
| `GET /ready` | `ready` (DB 연결 실패시 `503 SERVICE_UNAVAILABLE`) |
