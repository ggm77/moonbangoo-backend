-- SQLite 스키마, 서버를 시작할 때마다 실행되며 이미 있는 테이블은 건드리지 않는다.
-- Hibernate의 SQLite dialect가 자동 증가 id 컬럼을 올바르게 만들지 못해서 ddl-auto 대신 직접 관리한다.
-- 컬럼을 바꿀 때는 기존 DB 파일에 ALTER TABLE을 직접 실행하거나 DB 파일을 지우고 다시 만들어야 한다.

-- 카드팩
CREATE TABLE IF NOT EXISTS pack (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       VARCHAR(50) NOT NULL,
    image      VARCHAR(2048),
    icon       VARCHAR(30),
    active     BOOLEAN     NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL
);

-- 경품, 한 팩에만 속하고 수량은 경품마다 따로 관리 (condition은 예약어라 exchange_condition)
CREATE TABLE IF NOT EXISTS prize (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    pack_id            INTEGER     NOT NULL REFERENCES pack (id),
    name               VARCHAR(50) NOT NULL,
    rarity             VARCHAR(10) NOT NULL CHECK (rarity IN ('C', 'R', 'SR', 'UR')),
    description        VARCHAR(500),
    exchange_condition VARCHAR(200),
    total              INTEGER     NOT NULL CHECK (total >= 0),
    remaining          INTEGER     NOT NULL CHECK (remaining >= 0),
    created_at         TIMESTAMP   NOT NULL,
    updated_at         TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_prize_pack_id ON prize (pack_id);

-- 뽑기 기록, 팩과 경품을 삭제해도 남도록 팩, 경품은 아이디와 이름만 저장
CREATE TABLE IF NOT EXISTS draw (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    pack_id             INTEGER     NOT NULL,
    pack_name           VARCHAR(50) NOT NULL,
    status              VARCHAR(15) NOT NULL CHECK (status IN ('DRAWN', 'CONFIRMED', 'CANCELLED')),
    selected_prize_id   INTEGER,
    selected_prize_name VARCHAR(50),
    confirmed_at        TIMESTAMP,
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP   NOT NULL
);

-- 뽑기에서 받은 카드 5장 (넘기는 순서, 같은 경품이 여러 장일 수 있음)
CREATE TABLE IF NOT EXISTS draw_shown_prize (
    draw_id    INTEGER NOT NULL REFERENCES draw (id) ON DELETE CASCADE,
    card_order INTEGER NOT NULL CHECK (card_order >= 0),
    prize_id   INTEGER NOT NULL,
    PRIMARY KEY (draw_id, card_order)
);
