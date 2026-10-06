package com.seohamin.moonbangoo.domain.draw.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DrawResponseDto {

    //경품을 확정할 때 사용하는 뽑기 아이디
    private final Long drawId;

    //뽑힌 카드들 (넘기는 순서 = 등급 오름차순)
    private final List<DrawCardResponseDto> cards;

    public DrawResponseDto(final Long drawId, final List<DrawCardResponseDto> cards) {
        this.drawId = drawId;
        this.cards = cards;
    }
}
