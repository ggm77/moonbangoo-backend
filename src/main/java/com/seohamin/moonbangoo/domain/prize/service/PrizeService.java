package com.seohamin.moonbangoo.domain.prize.service;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;
    private final PackRepository packRepository;

    /**
     * 경품을 등록하는 메서드
     * 남은 수량은 처음 수량과 같게 시작
     * @param prizeRequestDto 경품 정보
     * @return 등록된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto createPrize(final PrizeRequestDto prizeRequestDto){

        final Prize prize = prizeRepository.save(Prize.builder()
                .pack(findPack(prizeRequestDto.getPackId()))
                .name(prizeRequestDto.getName())
                .rarity(toRarity(prizeRequestDto.getRarity()))
                .description(blankToNull(prizeRequestDto.getDescription()))
                .condition(blankToNull(prizeRequestDto.getCondition()))
                .total(prizeRequestDto.getTotal())
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
     * 경품 목록을 조회하는 메서드
     * @param packId 팩 아이디, null이면 전체 팩의 경품
     * @return 경품 목록 DTO
     */
    @Transactional(readOnly = true)
    public PrizeListResponseDto getPrizes(final Long packId){
        final List<Prize> prizes = packId == null
                ? prizeRepository.findAllWithPack()
                : prizeRepository.findAllByPackId(packId);

        return new PrizeListResponseDto(prizes.stream().map(PrizeResponseDto::new).toList());
    }

    /**
     * 경품 정보를 수정하는 메서드
     * 변경할 값만 변경, 남은 수량은 따로 수정
     * @param prizeId 경품 아이디
     * @param prizeRequestDto 수정할 정보
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto updatePrize(
            final Long prizeId,
            final PrizeRequestDto prizeRequestDto
    ){
        final Prize prize = findPrizeForUpdate(prizeId);

        if(prizeRequestDto.getPackId() != null){
            prize.updatePack(findPack(prizeRequestDto.getPackId()));
        }

        //필수 값은 빈 문자열로 바꿀 수 없음
        if(prizeRequestDto.getName() != null && !prizeRequestDto.getName().isBlank()){
            prize.updateName(prizeRequestDto.getName());
        }

        if(prizeRequestDto.getRarity() != null){
            prize.updateRarity(toRarity(prizeRequestDto.getRarity()));
        }

        //선택 값은 빈 문자열이면 지움
        if(prizeRequestDto.getDescription() != null){
            prize.updateDescription(blankToNull(prizeRequestDto.getDescription()));
        }

        if(prizeRequestDto.getCondition() != null){
            prize.updateCondition(blankToNull(prizeRequestDto.getCondition()));
        }

        //처음 수량은 남은 수량보다 작게 바꿀 수 없음
        if(prizeRequestDto.getTotal() != null){
            if(prizeRequestDto.getTotal() < prize.getRemaining()){
                throw new CustomException(ExceptionCode.INVALID_REQUEST);
            }
            prize.updateTotal(prizeRequestDto.getTotal());
        }

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품의 남은 수량을 수정하는 메서드
     * 처음 수량보다 많게 바꾸면 처음 수량도 같이 늘어남
     * @param prizeId 경품 아이디
     * @param prizeRemainingRequestDto 변경할 남은 수량
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto updateRemaining(
            final Long prizeId,
            final PrizeRemainingRequestDto prizeRemainingRequestDto
    ){
        final Prize prize = findPrizeForUpdate(prizeId);

        prize.updateRemaining(prizeRemainingRequestDto.getRemaining());

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품의 남은 수량을 늘리거나 줄이는 메서드
     * @param prizeId 경품 아이디
     * @param prizeAdjustRequestDto 증감할 수량 (음수면 줄임)
     * @return 수정된 경품 DTO
     */
    @Transactional
    public PrizeResponseDto adjustRemaining(
            final Long prizeId,
            final PrizeAdjustRequestDto prizeAdjustRequestDto
    ){
        final Prize prize = findPrizeForUpdate(prizeId);

        //int 범위를 넘는 값은 long으로 계산해서 걸러냄
        final long adjusted = (long) prize.getRemaining() + prizeAdjustRequestDto.getDelta();
        if(adjusted < 0 || adjusted > Integer.MAX_VALUE){
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        prize.updateRemaining((int) adjusted);

        return new PrizeResponseDto(prize);
    }

    /**
     * 경품을 삭제하는 메서드
     * 뽑기 기록에는 경품 이름이 남아있어서 삭제해도 기록은 유지됨
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

    //수량이 바뀔 수 있는 작업용 경품 조회 (행 잠금)
    private Prize findPrizeForUpdate(final Long prizeId){
        return prizeRepository.findByIdForUpdate(prizeId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRIZE_NOT_EXIST));
    }

    //팩 조회
    private Pack findPack(final Long packId){
        return packRepository.findById(packId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PACK_NOT_EXIST));
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
