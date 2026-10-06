package com.seohamin.moonbangoo.domain.draw.dto;

import com.seohamin.moonbangoo.domain.draw.entity.Draw;
import com.seohamin.moonbangoo.domain.draw.entity.DrawStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * 어드민용 뽑기 기록 (당첨 기록)
 */
@Getter
public class DrawRecordResponseDto {
    private final Long id;
    private final Long packId;
    private final String packName;
    private final DrawStatus status;

    //손님이 고른 경품과 확정 당시 이름
    private final Long selectedPrizeId;
    private final String selectedPrizeName;

    //그 뽑기에서 나왔던 카드 5장의 경품 아이디
    private final List<Long> shownPrizeIds;

    private final Instant confirmedAt;

    public DrawRecordResponseDto(final Draw draw) {
        this.id = draw.getId();
        this.packId = draw.getPackId();
        this.packName = draw.getPackName();
        this.status = draw.getStatus();
        this.selectedPrizeId = draw.getSelectedPrizeId();
        this.selectedPrizeName = draw.getSelectedPrizeName();
        this.shownPrizeIds = List.copyOf(draw.getShownPrizeIds());
        this.confirmedAt = draw.getConfirmedAt();
    }
}
