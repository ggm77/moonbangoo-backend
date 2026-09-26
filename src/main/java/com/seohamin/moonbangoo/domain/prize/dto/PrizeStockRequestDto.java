package com.seohamin.moonbangoo.domain.prize.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

/**
 * 경품 재고 수정 요청 DTO
 */
@Getter
public class PrizeStockRequestDto {

    //null이면 무제한
    @PositiveOrZero
    private Integer stock;
}
