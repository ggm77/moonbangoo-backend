package com.seohamin.moonbangoo.domain.prize.entity;

import com.seohamin.moonbangoo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 카드 뽑기 경품 엔티티
 * 사장님이 등록하고 확률, 재고를 관리함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "prize")
public class Prize extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //경품 이름 (ex. 10% 할인권)
    @Column(length = 50, nullable = false)
    private String name;

    //카드 등급
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Rarity rarity;

    //경품 카테고리 (ex. coupon, sticker, cafe, pack), 카드 아이콘에 사용
    @Column(length = 30, nullable = false)
    private String category;

    //경품 이미지 url
    @Column(length = 2048, nullable = true)
    private String image;

    //경품 설명
    @Column(length = 500, nullable = true)
    private String description;

    //교환 조건 문구 (ex. 1만원 이상 구매 시 사용)
    //condition은 MariaDB 예약어라 컬럼명 변경
    @Column(name = "exchange_condition", length = 200, nullable = true)
    private String condition;

    //뽑힐 확률 (0 ~ 1), 0이면 뽑히지 않음
    //뽑을 때는 뽑을 수 있는 경품들의 확률 합으로 나눠서 사용하므로 합이 1이 아니어도 동작함
    @Column(precision = 7, scale = 6, nullable = false)
    private BigDecimal probability;

    //남은 재고, null이면 무제한, 0이면 뽑히지 않음
    @Column(nullable = true)
    private Integer stock;

    @Builder
    public Prize(
            final String name,
            final Rarity rarity,
            final String category,
            final String image,
            final String description,
            final String condition,
            final BigDecimal probability,
            final Integer stock
    ){
        this.name = name;
        this.rarity = rarity;
        this.category = category;
        this.image = image;
        this.description = description;
        this.condition = condition;
        this.probability = probability;
        this.stock = stock;
    }

    //이름 변경
    public void updateName(final String name){
        this.name = name;
    }

    //등급 변경
    public void updateRarity(final Rarity rarity){
        this.rarity = rarity;
    }

    //카테고리 변경
    public void updateCategory(final String category){
        this.category = category;
    }

    //이미지 변경
    public void updateImage(final String image){
        this.image = image;
    }

    //설명 변경
    public void updateDescription(final String description){
        this.description = description;
    }

    //교환 조건 변경
    public void updateCondition(final String condition){
        this.condition = condition;
    }

    //확률 변경
    public void updateProbability(final BigDecimal probability){
        this.probability = probability;
    }

    //재고 변경 (null이면 무제한)
    public void updateStock(final Integer stock){
        this.stock = stock;
    }
}
