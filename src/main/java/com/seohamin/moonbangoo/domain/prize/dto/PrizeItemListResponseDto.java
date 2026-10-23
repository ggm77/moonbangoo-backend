package com.seohamin.moonbangoo.domain.prize.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PrizeItemListResponseDto {
    private final List<PrizeItemResponseDto> prizes;

    public PrizeItemListResponseDto(final List<PrizeItemResponseDto> prizes) {
        this.prizes = prizes;
    }
}
