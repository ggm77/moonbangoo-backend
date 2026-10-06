package com.seohamin.moonbangoo.domain.draw.service;

import com.seohamin.moonbangoo.domain.draw.dto.DrawCardResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawResponseDto;
import com.seohamin.moonbangoo.domain.draw.entity.Draw;
import com.seohamin.moonbangoo.domain.draw.entity.DrawStatus;
import com.seohamin.moonbangoo.domain.draw.repository.DrawRepository;
import com.seohamin.moonbangoo.domain.draw.service.drawing.PrizeDrawer;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawService {

    //카드팩 하나에 들어있는 카드 수
    public static final int CARD_COUNT = 5;

    private final PackRepository packRepository;
    private final PrizeRepository prizeRepository;
    private final DrawRepository drawRepository;
    private final PrizeDrawer prizeDrawer;

    /**
     * 팩에서 카드를 뽑는 메서드
     * 선택한 팩 안에서 남은 수량이 있는 경품만 수량에 비례해서 뽑음
     * 수량은 차감하지 않고, 받은 카드는 서버에 기록해서 확정할 때 사용함
     * @param packId 손님이 고른 팩 아이디
     * @return 뽑기 아이디와 뽑힌 카드 5장 (등급 오름차순)
     */
    @Transactional
    public DrawResponseDto draw(final Long packId){

        // 1) 팩 조회, 비활성 팩은 뽑을 수 없음
        final Pack pack = packRepository.findById(packId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PACK_NOT_EXIST));

        if(!pack.isActive()){
            throw new CustomException(ExceptionCode.PACK_INACTIVE);
        }

        // 2) 팩 안에서 뽑을 수 있는 경품 조회
        final List<Prize> candidates = prizeRepository.findDrawableByPackId(packId);

        // 3) 남은 수량에 비례해서 5장 뽑기
        final List<Prize> cards = prizeDrawer.draw(candidates, CARD_COUNT);

        // 4) 받은 카드 기록
        final Draw draw = drawRepository.save(new Draw(
                pack.getId(),
                pack.getName(),
                cards.stream().map(Prize::getId).toList()
        ));

        return new DrawResponseDto(draw.getId(), cards.stream().map(DrawCardResponseDto::new).toList());
    }

    /**
     * 뽑은 카드 중 가져갈 경품을 확정하는 메서드
     * 고른 경품의 남은 수량만 1 차감, 고르지 않은 카드는 차감하지 않음
     * @param drawId 뽑기 아이디
     * @param prizeId 고른 경품 아이디
     * @return 확정된 경품 카드
     */
    @Transactional
    public DrawCardResponseDto confirm(final Long drawId, final Long prizeId){

        // 1) 뽑기 조회 (행 잠금), 이미 확정했거나 취소된 뽑기는 다시 확정할 수 없음
        final Draw draw = drawRepository.findByIdForUpdate(drawId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DRAW_NOT_EXIST));

        if(draw.getStatus() != DrawStatus.DRAWN){
            throw new CustomException(ExceptionCode.DRAW_ALREADY_CONFIRMED);
        }

        // 2) 뽑은 카드에 있던 경품만 고를 수 있음
        if(!draw.hasShown(prizeId)){
            throw new CustomException(ExceptionCode.PRIZE_NOT_IN_DRAW);
        }

        // 3) 경품 조회 (행 잠금), 그 사이에 소진됐으면 실패
        final Prize prize = prizeRepository.findByIdForUpdate(prizeId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRIZE_NOT_EXIST));

        if(prize.getRemaining() <= 0){
            throw new CustomException(ExceptionCode.PRIZE_SOLD_OUT);
        }

        // 4) 수량 차감과 기록 확정
        prize.decreaseRemaining();
        draw.confirm(prize.getId(), prize.getName());

        return new DrawCardResponseDto(prize);
    }
}
