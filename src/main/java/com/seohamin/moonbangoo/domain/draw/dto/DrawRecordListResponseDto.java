package com.seohamin.moonbangoo.domain.draw.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DrawRecordListResponseDto {
    private final List<DrawRecordResponseDto> draws;

    public DrawRecordListResponseDto(final List<DrawRecordResponseDto> draws) {
        this.draws = draws;
    }
}
