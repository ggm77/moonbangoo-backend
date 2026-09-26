package com.seohamin.moonbangoo.domain.prize.dto;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 어드민용 경품 정보
 */
@Getter
public class PrizeResponseDto {
    private final Long id;
    private final String name;
    private final Rarity rarity;
    private final String category;
    private final String image;
    private final String description;
    private final String condition;
    private final BigDecimal probability;
    private final Integer stock;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PrizeResponseDto(final Prize prize) {
        this.id = prize.getId();
        this.name = prize.getName();
        this.rarity = prize.getRarity();
        this.category = prize.getCategory();
        this.image = prize.getImage();
        this.description = prize.getDescription();
        this.condition = prize.getCondition();
        this.probability = prize.getProbability();
        this.stock = prize.getStock();
        this.createdAt = prize.getCreatedAt();
        this.updatedAt = prize.getUpdatedAt();
    }
}
