package com.seohamin.moonbangoo.domain.prize.dto;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

/**
 * 손님에게 보여주는 경품 정보
 * 화면에 그리는 값만 담고 팩 정보나 생성/수정 시각 같은 관리용 값은 뺌
 */
@Getter
public class PrizeItemResponseDto {
    private final Long id;
    private final String name;
    private final Rarity rarity;
    private final String icon;
    private final String description;
    private final String condition;
    private final int remaining;
    private final int total;

    public PrizeItemResponseDto(final Prize prize) {
        this.id = prize.getId();
        this.name = prize.getName();
        this.rarity = prize.getRarity();
        this.icon = prize.getIcon();
        this.description = prize.getDescription();
        this.condition = prize.getCondition();
        this.remaining = prize.getRemaining();
        this.total = prize.getTotal();
    }
}
