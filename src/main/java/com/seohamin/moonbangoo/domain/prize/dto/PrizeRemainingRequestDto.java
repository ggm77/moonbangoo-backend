package com.seohamin.moonbangoo.domain.prize.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

/**
 * 경품 남은 수량 수정 요청 DTO
 */
@Getter
public class PrizeRemainingRequestDto {

    //처음 수량보다 크게 보내면 처음 수량도 같이 늘어남
    @NotNull
    @PositiveOrZero
    private Integer remaining;
}
