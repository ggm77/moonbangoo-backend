package com.seohamin.moonbangoo.domain.draw.dto;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

/**
 * 뽑기 결과 카드 1장
 * 사용자에게는 확률, 재고를 보여주지 않음
 */
@Getter
public class DrawCardResponseDto {
    private final Long id;
    private final String name;
    private final Rarity rarity;
    private final String category;
    private final String image;
    private final String description;
    private final String condition;

    public DrawCardResponseDto(final Prize prize) {
        this.id = prize.getId();
        this.name = prize.getName();
        this.rarity = prize.getRarity();
        this.category = prize.getCategory();
        this.image = prize.getImage();
        this.description = prize.getDescription();
        this.condition = prize.getCondition();
    }
}
