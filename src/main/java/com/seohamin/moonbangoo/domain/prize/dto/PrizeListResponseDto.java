package com.seohamin.moonbangoo.domain.prize.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class PrizeListResponseDto {
    private final List<PrizeResponseDto> prizes;

    //전체 경품 확률 합 (사장님이 합이 1인지 확인하는 용도)
    private final BigDecimal totalProbability;

    public PrizeListResponseDto(
            final List<PrizeResponseDto> prizes,
            final BigDecimal totalProbability
    ) {
        this.prizes = prizes;
        this.totalProbability = totalProbability;
    }
}
