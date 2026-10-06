package com.seohamin.moonbangoo.domain.prize.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PrizeListResponseDto {
    private final List<PrizeResponseDto> prizes;

    public PrizeListResponseDto(final List<PrizeResponseDto> prizes) {
        this.prizes = prizes;
    }
}
