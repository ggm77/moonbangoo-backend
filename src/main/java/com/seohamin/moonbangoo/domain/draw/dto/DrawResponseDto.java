package com.seohamin.moonbangoo.domain.draw.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DrawResponseDto {

    //뽑힌 카드들 (넘기는 순서 = 등급 오름차순)
    private final List<DrawCardResponseDto> cards;

    public DrawResponseDto(final List<DrawCardResponseDto> cards) {
        this.cards = cards;
    }
}
