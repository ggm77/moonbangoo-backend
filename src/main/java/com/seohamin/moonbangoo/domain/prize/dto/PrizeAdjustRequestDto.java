package com.seohamin.moonbangoo.domain.prize.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 경품 남은 수량 증감 요청 DTO
 */
@Getter
public class PrizeAdjustRequestDto {

    //양수면 늘리고 음수면 줄임, 결과가 0보다 작아지면 실패
    @NotNull
    private Integer delta;
}
