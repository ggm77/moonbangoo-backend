package com.seohamin.moonbangoo.domain.draw.service;

import com.seohamin.moonbangoo.domain.draw.dto.DrawRecordListResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawRecordResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.SummaryResponseDto;
import com.seohamin.moonbangoo.domain.draw.entity.Draw;
import com.seohamin.moonbangoo.domain.draw.entity.DrawStatus;
import com.seohamin.moonbangoo.domain.draw.repository.DrawRepository;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DrawAdminService {

    private final DrawRepository drawRepository;
    private final PrizeRepository prizeRepository;

    /**
     * 당첨 기록(확정된 뽑기)을 조회하는 메서드
     * @return 최근 기록 100개, 최신순
     */
    @Transactional(readOnly = true)
    public DrawRecordListResponseDto getRecords(){
        return new DrawRecordListResponseDto(
                drawRepository.findTop100ByStatusOrderByIdDesc(DrawStatus.CONFIRMED).stream()
                        .map(DrawRecordResponseDto::new)
                        .toList()
        );
    }

    /**
     * 확정된 뽑기를 되돌리는 메서드
     * 잘못 확정했거나 경품을 주지 못했을 때 사용, 차감했던 수량을 1 복원
     * 경품이 이미 삭제됐으면 수량 복원 없이 기록만 취소
     * @param drawId 뽑기 아이디
     * @return 취소된 기록 DTO
     */
    @Transactional
    public DrawRecordResponseDto cancel(final Long drawId){

        final Draw draw = drawRepository.findByIdForUpdate(drawId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DRAW_NOT_EXIST));

        if(draw.getStatus() != DrawStatus.CONFIRMED){
            throw new CustomException(ExceptionCode.DRAW_NOT_CONFIRMED);
        }

        prizeRepository.findByIdForUpdate(draw.getSelectedPrizeId())
                .ifPresent(Prize::increaseRemaining);

        draw.cancel();

        return new DrawRecordResponseDto(draw);
    }

    /**
     * 팩별, 등급별 남은 수량을 요약하는 메서드
     * @return 요약 DTO
     */
    @Transactional(readOnly = true)
    public SummaryResponseDto getSummary(){
        final List<Prize> prizes = prizeRepository.findAllWithPack();

        long remaining = 0;
        long total = 0;
        final Map<Rarity, long[]> byRarity = new LinkedHashMap<>();
        final Map<Pack, long[]> byPack = new LinkedHashMap<>();

        //등급은 값이 없어도 항상 보여줌
        Arrays.stream(Rarity.values()).forEach(rarity -> byRarity.put(rarity, new long[2]));

        for(final Prize prize : prizes){
            remaining += prize.getRemaining();
            total += prize.getTotal();

            final long[] rarityCount = byRarity.get(prize.getRarity());
            rarityCount[0] += prize.getRemaining();
            rarityCount[1] += prize.getTotal();

            final long[] packCount = byPack.computeIfAbsent(prize.getPack(), pack -> new long[2]);
            packCount[0] += prize.getRemaining();
            packCount[1] += prize.getTotal();
        }

        return new SummaryResponseDto(
                remaining,
                total,
                byRarity.entrySet().stream()
                        .map(e -> new SummaryResponseDto.RaritySummary(e.getKey(), e.getValue()[0], e.getValue()[1]))
                        .toList(),
                byPack.entrySet().stream()
                        .map(e -> new SummaryResponseDto.PackSummary(
                                e.getKey().getId(), e.getKey().getName(), e.getValue()[0], e.getValue()[1]))
                        .toList()
        );
    }

    /**
     * 이벤트를 처음 상태로 되돌리는 메서드
     * 모든 경품의 남은 수량을 처음 수량으로 되돌리고 뽑기 기록을 모두 삭제
     */
    @Transactional
    public void reset(){
        drawRepository.deleteAll();

        //삭제를 먼저 DB에 반영, 안 하면 아래 일괄 수정이 영속성 컨텍스트를 비우면서 삭제가 사라짐
        drawRepository.flush();

        prizeRepository.resetRemaining();
    }
}
