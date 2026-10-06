package com.seohamin.moonbangoo.domain.draw.entity;

import com.seohamin.moonbangoo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 뽑기 한 번의 기록
 * 손님이 받은 카드 5장을 서버가 기억해서, 확정할 때 그 안에서 고른 경품인지 검증하고 당첨 기록으로도 사용함
 * 팩, 경품을 삭제해도 기록이 남도록 연관관계 없이 아이디와 이름만 저장함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "draw")
public class Draw extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //뽑은 팩
    @Column(nullable = false)
    private Long packId;

    //뽑을 당시 팩 이름
    @Column(length = 50, nullable = false)
    private String packName;

    //받은 카드들의 경품 아이디 (넘기는 순서, 같은 경품이 여러 장일 수 있음)
    @ElementCollection
    @CollectionTable(name = "draw_shown_prize", joinColumns = @JoinColumn(name = "draw_id"))
    @OrderColumn(name = "card_order")
    @Column(name = "prize_id", nullable = false)
    private List<Long> shownPrizeIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private DrawStatus status;

    //고른 경품과 확정 당시 이름
    @Column(nullable = true)
    private Long selectedPrizeId;

    @Column(length = 50, nullable = true)
    private String selectedPrizeName;

    //확정 시각
    @Column(nullable = true)
    private Instant confirmedAt;

    public Draw(final Long packId, final String packName, final List<Long> shownPrizeIds) {
        this.packId = packId;
        this.packName = packName;
        this.shownPrizeIds = new ArrayList<>(shownPrizeIds);
        this.status = DrawStatus.DRAWN;
    }

    //받은 카드 중에 있는 경품인지
    public boolean hasShown(final Long prizeId) {
        return shownPrizeIds.contains(prizeId);
    }

    //경품 선택 확정
    public void confirm(final Long prizeId, final String prizeName) {
        this.status = DrawStatus.CONFIRMED;
        this.selectedPrizeId = prizeId;
        this.selectedPrizeName = prizeName;
        this.confirmedAt = Instant.now();
    }

    //확정 취소 (기록은 남김)
    public void cancel() {
        this.status = DrawStatus.CANCELLED;
    }
}
