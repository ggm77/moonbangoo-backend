package com.seohamin.moonbangoo.domain.draw.service;

import com.seohamin.moonbangoo.domain.draw.dto.DrawCardResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawResponseDto;
import com.seohamin.moonbangoo.domain.draw.service.drawing.PrizeDrawer;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawService {

    //카드팩 하나에 들어있는 카드 수
    public static final int CARD_COUNT = 5;

    private final PrizeRepository prizeRepository;
    private final PrizeDrawer prizeDrawer;

    /**
     * 카드팩을 뽑는 메서드
     * 확률이 0보다 크고 재고가 남은 경품 중에서 서로 다른 경품 5개를 확률에 따라 뽑음
     * @return 뽑힌 카드 5장 (등급 오름차순)
     */
    @Transactional(readOnly = true)
    public DrawResponseDto draw(){

        // 1) 뽑을 수 있는 경품 조회
        final List<Prize> candidates = prizeRepository.findDrawable();

        // 2) 확률에 따라 5장 뽑기
        final List<Prize> cards = prizeDrawer.draw(candidates, CARD_COUNT);

        return new DrawResponseDto(cards.stream().map(DrawCardResponseDto::new).toList());
    }
}
