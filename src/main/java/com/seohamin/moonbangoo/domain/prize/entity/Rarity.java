package com.seohamin.moonbangoo.domain.prize.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 카드 등급
 * 선언 순서가 낮은 등급 -> 높은 등급 순서 (뽑기 결과 정렬에 사용)
 */
@Getter
@AllArgsConstructor
public enum Rarity {

    C("일반"),
    R("레어"),
    SR("슈퍼레어"),
    UR("최고등급");

    private final String title;
}
