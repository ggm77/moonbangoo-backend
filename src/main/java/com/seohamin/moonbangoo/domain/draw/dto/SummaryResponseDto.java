package com.seohamin.moonbangoo.domain.draw.dto;

import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import lombok.Getter;

import java.util.List;

/**
 * 이벤트 진행 정도 요약 (남은 수량 / 처음 수량)
 */
@Getter
public class SummaryResponseDto {
    private final long remaining;
    private final long total;
    private final List<RaritySummary> rarities;
    private final List<PackSummary> packs;

    public SummaryResponseDto(
            final long remaining,
            final long total,
            final List<RaritySummary> rarities,
            final List<PackSummary> packs
    ) {
        this.remaining = remaining;
        this.total = total;
        this.rarities = rarities;
        this.packs = packs;
    }

    //등급별 수량
    public record RaritySummary(Rarity rarity, long remaining, long total) {}

    //팩별 수량
    public record PackSummary(Long packId, String name, long remaining, long total) {}
}
