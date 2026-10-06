package com.seohamin.moonbangoo.domain.pack.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PackListResponseDto {
    private final List<PackResponseDto> packs;

    public PackListResponseDto(final List<PackResponseDto> packs) {
        this.packs = packs;
    }
}
