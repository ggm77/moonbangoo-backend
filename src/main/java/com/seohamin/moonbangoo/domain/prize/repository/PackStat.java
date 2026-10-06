package com.seohamin.moonbangoo.domain.prize.repository;

/**
 * 팩별 경품 집계 (경품 수, 남은 수량 합, 처음 수량 합)
 */
public record PackStat(Long packId, Long prizeCount, Long remaining, Long total) {
}
