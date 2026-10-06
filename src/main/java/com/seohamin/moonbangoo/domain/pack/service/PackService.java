package com.seohamin.moonbangoo.domain.pack.service;

import com.seohamin.moonbangoo.domain.pack.dto.*;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
import com.seohamin.moonbangoo.domain.prize.repository.PackStat;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackService {

    private final PackRepository packRepository;
    private final PrizeRepository prizeRepository;

    /**
     * 팩을 등록하는 메서드
     * @param packRequestDto 팩 정보
     * @return 등록된 팩 DTO (경품 없이 시작)
     */
    @Transactional
    public PackResponseDto createPack(final PackRequestDto packRequestDto){

        final Pack pack = packRepository.save(Pack.builder()
                .name(packRequestDto.getName())
                .image(blankToNull(packRequestDto.getImage()))
                .icon(blankToNull(packRequestDto.getIcon()))
                .active(packRequestDto.getActive() == null || packRequestDto.getActive())
                .build());

        return new PackResponseDto(pack, 0, 0, 0);
    }

    /**
     * 팩 하나를 조회하는 메서드
     * @param packId 팩 아이디
     * @return 팩 DTO
     */
    @Transactional(readOnly = true)
    public PackResponseDto getPack(final Long packId){
        return toResponse(findPack(packId), statsByPackId());
    }

    /**
     * 전체 팩 목록을 조회하는 메서드 (비활성 팩 포함)
     * @return 팩 목록 DTO
     */
    @Transactional(readOnly = true)
    public PackListResponseDto getPacks(){
        final Map<Long, PackStat> stats = statsByPackId();

        return new PackListResponseDto(packRepository.findAllByOrderByIdAsc().stream()
                .map(pack -> toResponse(pack, stats))
                .toList());
    }

    /**
     * 손님에게 보여줄 팩 목록을 조회하는 메서드
     * 활성 팩만 보여주고, 남은 경품이 없으면 품절로 표시
     * @return 팩 목록 DTO
     */
    @Transactional(readOnly = true)
    public PackItemListResponseDto getAvailablePacks(){
        final Map<Long, PackStat> stats = statsByPackId();

        final List<PackItemResponseDto> packs = packRepository.findAllByActiveTrueOrderByIdAsc().stream()
                .map(pack -> {
                    final PackStat stat = stats.get(pack.getId());
                    return new PackItemResponseDto(pack, stat != null && stat.remaining() > 0);
                })
                .toList();

        return new PackItemListResponseDto(packs);
    }

    /**
     * 팩 정보를 수정하는 메서드
     * 변경할 값만 변경
     * @param packId 팩 아이디
     * @param packRequestDto 수정할 정보
     * @return 수정된 팩 DTO
     */
    @Transactional
    public PackResponseDto updatePack(
            final Long packId,
            final PackRequestDto packRequestDto
    ){
        final Pack pack = findPack(packId);

        //필수 값은 빈 문자열로 바꿀 수 없음
        if(packRequestDto.getName() != null && !packRequestDto.getName().isBlank()){
            pack.updateName(packRequestDto.getName());
        }

        //선택 값은 빈 문자열이면 지움
        if(packRequestDto.getImage() != null){
            pack.updateImage(blankToNull(packRequestDto.getImage()));
        }

        if(packRequestDto.getIcon() != null){
            pack.updateIcon(blankToNull(packRequestDto.getIcon()));
        }

        if(packRequestDto.getActive() != null){
            pack.updateActive(packRequestDto.getActive());
        }

        return toResponse(pack, statsByPackId());
    }

    /**
     * 팩을 삭제하는 메서드
     * 경품이 남아있으면 삭제할 수 없음, 경품을 먼저 삭제하거나 다른 팩으로 옮겨야 함
     * @param packId 팩 아이디
     */
    @Transactional
    public void deletePack(final Long packId){
        final Pack pack = findPack(packId);

        if(prizeRepository.existsByPackId(packId)){
            throw new CustomException(ExceptionCode.PACK_NOT_EMPTY);
        }

        packRepository.delete(pack);
    }

    //팩 조회
    private Pack findPack(final Long packId){
        return packRepository.findById(packId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PACK_NOT_EXIST));
    }

    //팩 아이디별 경품 집계
    private Map<Long, PackStat> statsByPackId(){
        return prizeRepository.findPackStats().stream()
                .collect(Collectors.toMap(PackStat::packId, Function.identity()));
    }

    //팩과 집계를 응답 DTO로 변환 (경품이 없는 팩은 집계가 없음)
    private PackResponseDto toResponse(final Pack pack, final Map<Long, PackStat> stats){
        final PackStat stat = stats.get(pack.getId());

        return stat == null
                ? new PackResponseDto(pack, 0, 0, 0)
                : new PackResponseDto(pack, stat.prizeCount(), stat.remaining(), stat.total());
    }

    //빈 문자열은 null로 저장
    private String blankToNull(final String value){
        return value == null || value.isBlank() ? null : value;
    }
}
