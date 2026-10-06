# 어른문방구 API 명세서

- 작성일: 2026-09-26
- 최종 갱신: 2026-10-06
- 범위: 팩·경품 관리(어드민), 카드 뽑기와 경품 확정, 당첨 기록, 상태 확인 API

## 1. 개념

- **팩**: 손님이 고르는 카드팩이다. 사장님이 팩을 만들고, 팩 안에 경품을 넣어 구성한다.
- **경품**: 정확히 한 팩에 속한다. 수량(`total`, `remaining`)은 경품마다 따로 관리하며, 이름이 같은 경품이 다른 팩에 있어도 수량을 공유하지 않는다.
- **뽑기 흐름**
  1. 손님이 팩을 고른다. `POST /api/v1/packs/{packId}/draw` → 그 팩 안에서 카드 5장. **수량은 차감하지 않는다.**
  2. 손님이 5장 중 1장을 고른다. `POST /api/v1/draws/{drawId}/confirm` → 고른 경품의 `remaining`만 1 차감한다.
  3. 연출 도중 새로고침·이탈하면 확정을 호출하지 않으므로 아무것도 차감되지 않는다.

## 2. 공통 규칙

| 항목 | 규칙 |
| --- | --- |
| 서비스 API 접두사 | `/api/v1` |
| 상태 확인 경로 | `/ping`, `/ready` — `/api/v1`을 붙이지 않는다. |
| 요청 본문 | `Content-Type: application/json` |
| 성공 응답 | 응답 객체를 바로 반환한다. 공통 래퍼는 없다. |
| 생성 성공 | 팩·경품 등록, 카드 뽑기, 경품 확정도 `200 OK`를 반환한다. |
| 본문 없는 성공 | `204 No Content` |
| ID | JSON 정수, 서버 타입은 `Long` |
| 일시 | ISO 8601 UTC 문자열 (예: `2026-09-26T06:00:00Z`) |

### 인증

인증은 없다. 서버는 가게 내부망에서만 접속하는 것을 전제로 하며, 모든 API(`/api/v1/admin/**` 포함)를 인증 없이 호출할 수 있다. 서버 포트를 외부에 공개하지 않는다.

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
| `INVALID_REQUEST` | 400 | 필수 값 누락, 길이·범위 위반, 수량이 0 미만이 되는 증감, 남은 수량보다 작은 처음 수량, 확인 값 없는 초기화 |
| `INVALID_ENUM_VALUE` | 400 | 없는 등급 값 |
| `PACK_NOT_EXIST` | 400 | 팩 없음 |
| `PRIZE_NOT_EXIST` | 400 | 경품 없음 |
| `DRAW_NOT_EXIST` | 400 | 뽑기 기록 없음 |
| `PRIZE_NOT_IN_DRAW` | 400 | 뽑은 카드에 없던 경품을 고름 |
| `PACK_NOT_EMPTY` | 409 | 경품이 남아있는 팩 삭제 |
| `PACK_INACTIVE` | 409 | 비활성 팩에서 뽑기 |
| `PACK_SOLD_OUT` | 409 | 팩에 남은 경품이 없음 |
| `PRIZE_SOLD_OUT` | 409 | 확정하려는 경품이 그 사이에 소진됨 |
| `DRAW_ALREADY_CONFIRMED` | 409 | 이미 확정했거나 취소된 뽑기를 다시 확정 |
| `DRAW_NOT_CONFIRMED` | 409 | 확정되지 않은 뽑기를 되돌림 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 |
| `SERVICE_UNAVAILABLE` | 503 | DB 연결 실패 (`/ready`) |

### 등급

| 값 | 이름 |
| --- | --- |
| `C` | 일반 |
| `R` | 레어 |
| `SR` | 슈퍼레어 |
| `UR` | 최고등급 |

## 3. API 목록

| 메서드 | 경로 | 기능 | 성공 |
| --- | --- | --- | --- |
| GET | `/api/v1/packs` | 손님이 고를 팩 목록 | 200 |
| POST | `/api/v1/packs/{packId}/draw` | 팩에서 카드 뽑기 (5장, 수량 차감 없음) | 200 |
| POST | `/api/v1/draws/{drawId}/confirm` | 가져갈 경품 확정 (1 차감) | 200 |
| POST | `/api/v1/admin/pack` | 팩 등록 | 200 |
| GET | `/api/v1/admin/packs` | 팩 목록 (비활성 포함, 수량 합 포함) | 200 |
| GET | `/api/v1/admin/pack/{id}` | 팩 조회 | 200 |
| PATCH | `/api/v1/admin/pack/{id}` | 팩 수정 | 200 |
| DELETE | `/api/v1/admin/pack/{id}` | 팩 삭제 | 204 |
| POST | `/api/v1/admin/prize` | 경품 등록 | 200 |
| GET | `/api/v1/admin/prizes` | 경품 목록 (`packId`로 필터) | 200 |
| GET | `/api/v1/admin/prize/{id}` | 경품 조회 | 200 |
| PATCH | `/api/v1/admin/prize/{id}` | 경품 정보 수정 | 200 |
| PATCH | `/api/v1/admin/prize/{id}/remaining` | 남은 수량 설정 | 200 |
| PATCH | `/api/v1/admin/prize/{id}/adjust` | 남은 수량 증감 | 200 |
| DELETE | `/api/v1/admin/prize/{id}` | 경품 삭제 | 204 |
| GET | `/api/v1/admin/draws` | 당첨 기록 (최근 100개) | 200 |
| POST | `/api/v1/admin/draw/{id}/cancel` | 당첨 기록 되돌리기 (수량 1 복원) | 200 |
| GET | `/api/v1/admin/summary` | 팩별·등급별 남은 수량 요약 | 200 |
| POST | `/api/v1/admin/reset` | 이벤트 초기화 | 204 |
| GET | `/ping` | 서버 상태 확인 | 200 |
| GET | `/ready` | DB 연결 상태 확인 | 200 |

## 4. 손님용 API

### GET /api/v1/packs

활성 팩만 등록 순서대로 반환한다. 팩 안에 남은 경품이 없으면 `available`이 `false`이므로 "품절"로 표시한다. 모든 팩이 `available: false`이면 "이벤트 종료"를 보여주면 된다. 경품 수량은 응답에 포함하지 않는다.

```json
{
  "packs": [
    { "id": 1, "name": "베이커리 이용권", "image": "https://example.com/bakery.png", "icon": "bakery", "available": true }
  ]
}
```

`image`, `icon`은 `null`일 수 있다.

### POST /api/v1/packs/{packId}/draw

요청 본문 없음. 선택한 팩 안에서 남은 수량이 1 이상인 경품만 대상으로 카드 5장을 뽑는다. **수량은 차감하지 않는다.** 받은 카드는 서버에 기록되며 확정에서 사용한다.

- 남은 수량에 비례해서 뽑는다. 남은 수량이 많은 경품일수록 잘 나온다.
- 경품이 5종 이상이면 5장 모두 서로 다른 경품이다.
- 경품이 5종 미만이면 모든 종류를 한 장씩 넣고 남은 자리를 중복으로 채운다. 같은 경품이 여러 장이면 `id`가 같다. 한 장만 가져가고 한 장만 차감하므로 남은 수량보다 많이 보여도 된다.
- 결과는 등급 오름차순(C → UR)이며 이 순서대로 카드를 넘기면 된다.

응답:

```json
{
  "drawId": 12,
  "cards": [
    {
      "id": 1,
      "name": "5% 할인권",
      "rarity": "C",
      "description": "매장 전 품목 5% 할인\n다른 할인과 중복 적용 불가",
      "condition": "1만원 이상 구매 시 사용",
      "packId": 3,
      "packName": "할인권",
      "packImage": null,
      "packIcon": "coupon"
    }
  ]
}
```

`cards`는 항상 5개다(위 예시는 1개만 표시). `description`, `condition`, `packImage`, `packIcon`은 `null`일 수 있다. 수량은 응답에 포함하지 않는다.

에러: `PACK_NOT_EXIST`(400), `PACK_INACTIVE`(409), 남은 경품이 없으면 `PACK_SOLD_OUT`(409).

### POST /api/v1/draws/{drawId}/confirm

손님이 5장 중 가져갈 1장을 확정한다. **고른 경품의 남은 수량만 1 차감**하고 고르지 않은 카드는 차감하지 않는다.

```json
{ "prizeId": 1 }
```

응답은 확정된 경품 카드 1장이며 형식은 `cards`의 원소와 같다.

에러:

- `DRAW_NOT_EXIST`(400): 없는 뽑기
- `PRIZE_NOT_IN_DRAW`(400): 그 뽑기의 카드에 없던 경품
- `DRAW_ALREADY_CONFIRMED`(409): 이미 확정(또는 취소)된 뽑기. 두 번 눌러도 한 번만 차감된다.
- `PRIZE_SOLD_OUT`(409): 뽑은 뒤 확정 전에 다른 손님이 마지막 수량을 가져가거나 사장님이 수량을 줄여 소진됨. 다시 뽑아야 한다.

동시에 여러 손님이 같은 경품을 확정해도 남은 수량만큼만 성공한다.

## 5. 팩 관리

### 팩 응답 형식

```json
{
  "id": 1,
  "name": "베이커리 이용권",
  "image": "https://example.com/bakery.png",
  "icon": "bakery",
  "active": true,
  "prizeCount": 2,
  "remaining": 15,
  "total": 15,
  "createdAt": "2026-10-06T06:00:00Z",
  "updatedAt": "2026-10-06T06:00:00Z"
}
```

`prizeCount`는 팩 안의 경품 수, `remaining`, `total`은 팩 안 경품들의 남은 수량 합과 처음 수량 합이다.

### POST /api/v1/admin/pack

| 필드 | 타입 | 필수 | 조건 |
| --- | --- | --- | --- |
| `name` | string | 예 | 최대 50자 |
| `image` | string | 아니오 | 이미지 URL, 최대 2048자 |
| `icon` | string | 아니오 | 카드 우상단 아이콘 키, 최대 30자 |
| `active` | boolean | 아니오 | 생략하면 `true`. `false`면 손님에게 보이지 않고 뽑을 수 없다. |

```json
{ "name": "베이커리 이용권", "image": "https://example.com/bakery.png", "icon": "bakery" }
```

### GET /api/v1/admin/packs

비활성 팩을 포함해 등록 순서대로 반환한다.

```json
{ "packs": [ { "...": "팩 응답 형식" } ] }
```

### GET /api/v1/admin/pack/{id}

팩 하나를 반환한다.

### PATCH /api/v1/admin/pack/{id}

`name`, `image`, `icon`, `active` 중 바꿀 값만 보낸다. 조건은 등록과 같다.

- 보내지 않은 필드와 `null`은 변경하지 않는다.
- `image`, `icon`에 빈 문자열(`""`)을 보내면 값이 지워진다.

```json
{ "name": "빵집 이용권", "active": false }
```

### DELETE /api/v1/admin/pack/{id}

`204 No Content`. 경품이 남아있는 팩은 `409 PACK_NOT_EMPTY`이므로 경품을 먼저 삭제하거나 다른 팩으로 옮겨야 한다.

## 6. 경품 관리

### 경품 응답 형식

경품 등록·조회·수정·수량 변경 API는 모두 아래 형식을 반환한다.

```json
{
  "id": 1,
  "packId": 3,
  "packName": "할인권",
  "name": "10% 할인권",
  "rarity": "R",
  "description": "매장 전 품목 10% 할인",
  "condition": "2만원 이상 구매 시 사용",
  "total": 10,
  "remaining": 10,
  "createdAt": "2026-10-06T06:00:00Z",
  "updatedAt": "2026-10-06T06:00:00Z"
}
```

`total`은 처음 등록한 수량, `remaining`은 남은 수량이다. `remaining`이 `0`이면 뽑히지 않는다.

### POST /api/v1/admin/prize

| 필드 | 타입 | 필수 | 조건 |
| --- | --- | --- | --- |
| `packId` | integer | 예 | 경품이 들어갈 팩 |
| `name` | string | 예 | 최대 50자 |
| `rarity` | string | 예 | `C`, `R`, `SR`, `UR` |
| `description` | string | 아니오 | 최대 500자 |
| `condition` | string | 아니오 | 교환 조건 문구, 최대 200자 |
| `total` | integer | 예 | 0 이상. 남은 수량도 같은 값으로 시작한다. |

```json
{
  "packId": 5,
  "name": "부스터팩 1팩",
  "rarity": "UR",
  "description": "인기 카드 부스터팩 1팩",
  "condition": "현장 즉시 증정",
  "total": 10
}
```

### GET /api/v1/admin/prizes

등록 순서대로 경품을 반환한다. `packId` 쿼리를 주면 그 팩의 경품만 반환한다. (`/api/v1/admin/prizes?packId=3`)

```json
{ "prizes": [ { "...": "경품 응답 형식" } ] }
```

### GET /api/v1/admin/prize/{id}

경품 하나를 반환한다.

### PATCH /api/v1/admin/prize/{id}

`packId`, `name`, `rarity`, `description`, `condition`, `total` 중 바꿀 값만 보낸다. 조건은 등록과 같다.

- 보내지 않은 필드와 `null`은 변경하지 않는다.
- `description`, `condition`에 빈 문자열(`""`)을 보내면 값이 지워진다.
- `packId`를 보내면 경품이 다른 팩으로 이동한다. 수량은 그대로 가져간다.
- `total`은 현재 `remaining`보다 작게 바꿀 수 없다(`INVALID_REQUEST`).

```json
{ "name": "15% 할인권", "description": "" }
```

### PATCH /api/v1/admin/prize/{id}/remaining

```json
{ "remaining": 30 }
```

`remaining`은 필수, 0 이상. `total`보다 크게 설정하면 `total`도 같은 값으로 늘어난다(재고 보충).

### PATCH /api/v1/admin/prize/{id}/adjust

```json
{ "delta": -1 }
```

남은 수량을 `delta`만큼 늘리거나(양수) 줄인다(음수). 결과가 0보다 작아지면 `INVALID_REQUEST`. `total`보다 많아지면 `total`도 같이 늘어난다.

### DELETE /api/v1/admin/prize/{id}

`204 No Content`. 당첨 기록에는 경품 이름이 남아있어 삭제해도 기록은 유지된다.

## 7. 당첨 기록과 이벤트 관리

### GET /api/v1/admin/draws

확정된(`CONFIRMED`) 뽑기를 최신순으로 최대 100개 반환한다.

```json
{
  "draws": [
    {
      "id": 12,
      "packId": 3,
      "packName": "할인권",
      "status": "CONFIRMED",
      "selectedPrizeId": 1,
      "selectedPrizeName": "5% 할인권",
      "shownPrizeIds": [1, 4, 7, 8, 9],
      "confirmedAt": "2026-10-06T06:00:00Z"
    }
  ]
}
```

`shownPrizeIds`는 그 뽑기에서 손님이 받은 카드 5장의 경품 아이디(넘긴 순서)다. 팩이나 경품을 삭제해도 기록은 남는다.

### POST /api/v1/admin/draw/{id}/cancel

확정된 뽑기를 되돌린다. 차감했던 수량을 1 복원하고 기록 상태를 `CANCELLED`로 바꾼 뽑기 기록을 반환한다. 경품이 이미 삭제됐으면 수량 복원 없이 기록만 취소한다. 확정되지 않은 뽑기는 `409 DRAW_NOT_CONFIRMED`.

### GET /api/v1/admin/summary

이벤트 진행 정도를 반환한다. 등급은 값이 없어도 4개 모두 나온다.

```json
{
  "remaining": 9,
  "total": 10,
  "rarities": [
    { "rarity": "C", "remaining": 8, "total": 9 },
    { "rarity": "R", "remaining": 0, "total": 0 },
    { "rarity": "SR", "remaining": 0, "total": 0 },
    { "rarity": "UR", "remaining": 1, "total": 1 }
  ],
  "packs": [
    { "packId": 3, "name": "카드", "remaining": 9, "total": 10 }
  ]
}
```

`packs`에는 경품이 있는 팩만 나온다.

### POST /api/v1/admin/reset

모든 경품의 `remaining`을 `total`로 되돌리고 뽑기 기록을 모두 삭제한다. **되돌릴 수 없는 작업**이라 실수로 호출하지 않도록 확인 값을 요구한다.

```json
{ "confirm": true }
```

`confirm`이 `true`가 아니면 `400 INVALID_REQUEST`. 성공하면 `204 No Content`.

## 8. 상태 확인

| 경로 | 성공 응답 |
| --- | --- |
| `GET /ping` | `pong` |
| `GET /ready` | `ready` (DB 연결 실패시 `503 SERVICE_UNAVAILABLE`) |
