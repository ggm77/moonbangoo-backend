package com.seohamin.moonbangoo.domain.prize.entity;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드 뽑기 경품 엔티티
 * 한 팩에만 속하고, 수량은 경품마다 따로 관리함 (같은 이름의 경품이 다른 팩에 있어도 수량을 공유하지 않음)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "prize")
public class Prize extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //경품이 들어있는 팩
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pack_id", nullable = false)
    private Pack pack;

    //경품 이름 (ex. 10% 할인권)
    @Column(length = 50, nullable = false)
    private String name;

    //카드 등급
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Rarity rarity;

    //경품 설명
    @Column(length = 500, nullable = true)
    private String description;

    //교환 조건 문구 (ex. 1만원 이상 구매 시 사용)
    //condition은 MariaDB 예약어라 컬럼명 변경
    @Column(name = "exchange_condition", length = 200, nullable = true)
    private String condition;

    //처음 등록한 수량
    @Column(nullable = false)
    private int total;

    //남은 수량, 0이면 뽑히지 않음, 남은 수량이 많을수록 잘 뽑힘
    @Column(nullable = false)
    private int remaining;

    @Builder
    public Prize(
            final Pack pack,
            final String name,
            final Rarity rarity,
            final String description,
            final String condition,
            final int total
    ){
        this.pack = pack;
        this.name = name;
        this.rarity = rarity;
        this.description = description;
        this.condition = condition;
        this.total = total;
        this.remaining = total;
    }

    //팩 이동 (수량은 그대로 가져감)
    public void updatePack(final Pack pack){
        this.pack = pack;
    }

    //이름 변경
    public void updateName(final String name){
        this.name = name;
    }

    //등급 변경
    public void updateRarity(final Rarity rarity){
        this.rarity = rarity;
    }

    //설명 변경
    public void updateDescription(final String description){
        this.description = description;
    }

    //교환 조건 변경
    public void updateCondition(final String condition){
        this.condition = condition;
    }

    //처음 수량 변경 (남은 수량보다 작게 바꿀 수 없음, 서비스에서 검증)
    public void updateTotal(final int total){
        this.total = total;
    }

    //남은 수량 변경, 처음 수량보다 많아지면 처음 수량도 같이 늘림 (재고 보충)
    public void updateRemaining(final int remaining){
        this.remaining = remaining;
        if(remaining > total){
            this.total = remaining;
        }
    }

    //뽑기 확정시 1 차감 (서비스에서 남은 수량 검증)
    public void decreaseRemaining(){
        this.remaining--;
    }

    //확정 취소시 1 복원
    public void increaseRemaining(){
        updateRemaining(remaining + 1);
    }
}
