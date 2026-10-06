package com.seohamin.moonbangoo.domain.draw.dto;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

/**
 * 뽑기 결과 카드 1장
 * 사용자에게는 수량을 보여주지 않음
 * 같은 경품이 여러 장 나올 수 있고, 그 경우 id가 같음
 */
@Getter
public class DrawCardResponseDto {
    private final Long id;
    private final String name;
    private final Rarity rarity;
    private final String description;
    private final String condition;
    private final Long packId;
    private final String packName;
    private final String packImage;
    private final String packIcon;

    public DrawCardResponseDto(final Prize prize) {
        final Pack pack = prize.getPack();

        this.id = prize.getId();
        this.name = prize.getName();
        this.rarity = prize.getRarity();
        this.description = prize.getDescription();
        this.condition = prize.getCondition();
        this.packId = pack.getId();
        this.packName = pack.getName();
        this.packImage = pack.getImage();
        this.packIcon = pack.getIcon();
    }
}
