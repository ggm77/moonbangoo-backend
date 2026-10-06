package com.seohamin.moonbangoo.domain.pack.dto;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import lombok.Getter;

import java.time.Instant;

/**
 * 어드민용 팩 정보
 */
@Getter
public class PackResponseDto {
    private final Long id;
    private final String name;
    private final String image;
    private final String icon;
    private final boolean active;

    //팩에 들어있는 경품 수
    private final long prizeCount;

    //팩 안의 경품 남은 수량 합
    private final long remaining;

    //팩 안의 경품 처음 수량 합
    private final long total;

    private final Instant createdAt;
    private final Instant updatedAt;

    public PackResponseDto(
            final Pack pack,
            final long prizeCount,
            final long remaining,
            final long total
    ) {
        this.id = pack.getId();
        this.name = pack.getName();
        this.image = pack.getImage();
        this.icon = pack.getIcon();
        this.active = pack.isActive();
        this.prizeCount = prizeCount;
        this.remaining = remaining;
        this.total = total;
        this.createdAt = pack.getCreatedAt();
        this.updatedAt = pack.getUpdatedAt();
    }
}
