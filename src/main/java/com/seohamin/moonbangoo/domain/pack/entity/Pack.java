package com.seohamin.moonbangoo.domain.pack.entity;

import com.seohamin.moonbangoo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드팩 엔티티
 * 손님이 고르는 단위이고, 사장님이 팩 안에 경품을 넣어 구성함
 * 카드는 선택한 팩에 들어있는 경품 중에서만 뽑힘
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pack")
public class Pack extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //팩 이름 (ex. 베이커리 이용권)
    @Column(length = 50, nullable = false)
    private String name;

    //팩 이미지 url, 팩에 들어있는 경품 카드의 일러스트로 사용
    @Column(length = 2048, nullable = true)
    private String image;

    //카드 우상단 아이콘 키
    @Column(length = 30, nullable = true)
    private String icon;

    //false면 손님에게 보이지 않고 뽑을 수 없음
    @Column(nullable = false)
    private boolean active;

    @Builder
    public Pack(
            final String name,
            final String image,
            final String icon,
            final boolean active
    ){
        this.name = name;
        this.image = image;
        this.icon = icon;
        this.active = active;
    }

    //이름 변경
    public void updateName(final String name){
        this.name = name;
    }

    //이미지 변경
    public void updateImage(final String image){
        this.image = image;
    }

    //아이콘 변경
    public void updateIcon(final String icon){
        this.icon = icon;
    }

    //활성 여부 변경
    public void updateActive(final boolean active){
        this.active = active;
    }
}
