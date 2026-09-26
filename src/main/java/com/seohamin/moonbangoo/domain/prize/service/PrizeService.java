package com.seohamin.moonbangoo.domain.prize.service;

import com.seohamin.moonbangoo.domain.prize.dto.*;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import com.seohamin.moonbangoo.global.util.EnumUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;

    /**
     * 경품을 등록하는 메서드
     * @param prizeRequestDto 경품 정보
     * @return 등록된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto createPrize(final PrizeRequestDto prizeRequestDto){

        final Prize prize = prizeRepository.save(Prize.builder()
                .name(prizeRequestDto.getName())
                .rarity(toRarity(prizeRequestDto.getRarity()))
                .category(prizeRequestDto.getCategory())
                .image(blankToNull(prizeRequestDto.getImage()))
                .description(blankToNull(prizeRequestDto.getDescription()))
                .condition(blankToNull(prizeRequestDto.getCondition()))
                .probability(prizeRequestDto.getProbability())
                .stock(prizeRequestDto.getStock())
                .build());

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품 하나를 조회하는 메서드
     * @param prizeId 경품 아이디
     * @return 경품 DTO
     */
    @Transactional(readOnly = true)
    public PrizeResponseDto getPrize(final Long prizeId){
        return new PrizeResponseDto(findPrize(prizeId));
    }

    /**
     * 전체 경품 목록과 확률 합을 조회하는 메서드
     * @return 경품 목록 DTO
     */
    @Transactional(readOnly = true)
    public PrizeListResponseDto getPrizes(){
        final List<Prize> prizes = prizeRepository.findAllByOrderByIdAsc();

        final BigDecimal totalProbability = prizes.stream()
                .map(Prize::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PrizeListResponseDto(
                prizes.stream().map(PrizeResponseDto::new).toList(),
                totalProbability
        );
    }

    /**
     * 경품 정보를 수정하는 메서드
     * 변경할 값만 변경, 확률과 재고는 따로 수정
     * @param prizeId 경품 아이디
     * @param prizeRequestDto 수정할 정보
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto updatePrize(
            final Long prizeId,
            final PrizeRequestDto prizeRequestDto
    ){
        final Prize prize = findPrize(prizeId);

        //필수 값은 빈 문자열로 바꿀 수 없음
        if(prizeRequestDto.getName() != null && !prizeRequestDto.getName().isBlank()){
            prize.updateName(prizeRequestDto.getName());
        }

        if(prizeRequestDto.getRarity() != null){
            prize.updateRarity(toRarity(prizeRequestDto.getRarity()));
        }

        if(prizeRequestDto.getCategory() != null && !prizeRequestDto.getCategory().isBlank()){
            prize.updateCategory(prizeRequestDto.getCategory());
        }

        //선택 값은 빈 문자열이면 지움
        if(prizeRequestDto.getImage() != null){
            prize.updateImage(blankToNull(prizeRequestDto.getImage()));
        }

        if(prizeRequestDto.getDescription() != null){
            prize.updateDescription(blankToNull(prizeRequestDto.getDescription()));
        }

        if(prizeRequestDto.getCondition() != null){
            prize.updateCondition(blankToNull(prizeRequestDto.getCondition()));
        }

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품의 확률을 수정하는 메서드
     * @param prizeId 경품 아이디
     * @param prizeProbabilityRequestDto 변경할 확률
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto updateProbability(
            final Long prizeId,
            final PrizeProbabilityRequestDto prizeProbabilityRequestDto
    ){
        final Prize prize = findPrize(prizeId);

        prize.updateProbability(prizeProbabilityRequestDto.getProbability());

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품의 재고를 수정하는 메서드
     * @param prizeId 경품 아이디
     * @param prizeStockRequestDto 변경할 재고 (null이면 무제한)
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto updateStock(
            final Long prizeId,
            final PrizeStockRequestDto prizeStockRequestDto
    ){
        final Prize prize = findPrize(prizeId);

        prize.updateStock(prizeStockRequestDto.getStock());

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품을 삭제하는 메서드
     * @param prizeId 경품 아이디
     */
    @Transactional
    public void deletePrize(final Long prizeId){
        prizeRepository.delete(findPrize(prizeId));
    }

    //경품 조회
    private Prize findPrize(final Long prizeId){
        return prizeRepository.findById(prizeId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRIZE_NOT_EXIST));
    }

    //문자열을 등급으로 변환
    private Rarity toRarity(final String rarity){
        return EnumUtil.toEnum(Rarity.class, rarity)
                .orElseThrow(() -> new CustomException(ExceptionCode.INVALID_ENUM_VALUE));
    }

    //빈 문자열은 null로 저장
    private String blankToNull(final String value){
        return value == null || value.isBlank() ? null : value;
    }
}
