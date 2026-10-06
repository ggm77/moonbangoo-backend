package com.seohamin.moonbangoo.domain.pack.dto;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import lombok.Getter;

/**
 * 손님에게 보여주는 팩 정보
 * 경품 수량은 보여주지 않고 뽑을 수 있는지만 알려줌
 */
@Getter
public class PackItemResponseDto {
    private final Long id;
    private final String name;
    private final String image;
    private final String icon;

    //남은 경품이 있어서 뽑을 수 있는지 (false면 품절)
    private final boolean available;

    public PackItemResponseDto(final Pack pack, final boolean available) {
        this.id = pack.getId();
        this.name = pack.getName();
        this.image = pack.getImage();
        this.icon = pack.getIcon();
        this.available = available;
    }
}
