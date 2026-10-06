package com.seohamin.moonbangoo.domain.prize.dto;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

import java.time.Instant;

/**
 * 어드민용 경품 정보
 */
@Getter
public class PrizeResponseDto {
    private final Long id;
    private final Long packId;
    private final String packName;
    private final String name;
    private final Rarity rarity;
    private final String description;
    private final String condition;
    private final int total;
    private final int remaining;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PrizeResponseDto(final Prize prize) {
        this.id = prize.getId();
        this.packId = prize.getPack().getId();
        this.packName = prize.getPack().getName();
        this.name = prize.getName();
        this.rarity = prize.getRarity();
        this.description = prize.getDescription();
        this.condition = prize.getCondition();
        this.total = prize.getTotal();
        this.remaining = prize.getRemaining();
        this.createdAt = prize.getCreatedAt();
        this.updatedAt = prize.getUpdatedAt();
    }
}
