package com.seohamin.moonbangoo.domain.pack.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PackItemListResponseDto {
    private final List<PackItemResponseDto> packs;

    public PackItemListResponseDto(final List<PackItemResponseDto> packs) {
        this.packs = packs;
    }
}
