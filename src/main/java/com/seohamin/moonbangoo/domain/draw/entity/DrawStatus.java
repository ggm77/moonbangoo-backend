package com.seohamin.moonbangoo.domain.draw.entity;

/**
 * 뽑기 상태
 */
public enum DrawStatus {

    //카드 5장을 받았고 아직 하나도 고르지 않음 (수량 차감 없음)
    DRAWN,

    //경품을 골라서 수량이 1 차감됨
    CONFIRMED,

    //확정을 사장님이 되돌려서 수량이 복원됨
    CANCELLED
}
