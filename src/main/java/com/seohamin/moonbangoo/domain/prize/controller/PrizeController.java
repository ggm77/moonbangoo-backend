package com.seohamin.moonbangoo.domain.prize.controller;

import com.seohamin.moonbangoo.domain.prize.dto.*;
import com.seohamin.moonbangoo.domain.prize.service.PrizeService;
import com.seohamin.moonbangoo.global.validation.Create;
import com.seohamin.moonbangoo.global.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사장님이 경품을 관리하는 API
 * 인증 없음, 가게 내부망에서만 접속하는 것을 전제로 함
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class PrizeController {

    private final PrizeService prizeService;

    //경품 등록 API
    @PostMapping("/prize")
    public ResponseEntity<PrizeResponseDto> createPrize(
            @Validated(Create.class) @RequestBody final PrizeRequestDto prizeRequestDto
    ){
        return ResponseEntity.ok(prizeService.createPrize(prizeRequestDto));
    }

    //경품 목록 조회 API (packId를 주면 해당 팩의 경품만)
    @GetMapping("/prizes")
    public ResponseEntity<PrizeListResponseDto> getPrizes(
            @RequestParam(required = false) final Long packId
    ){
        return ResponseEntity.ok(prizeService.getPrizes(packId));
    }

    //경품 조회 API
    @GetMapping("/prize/{id}")
    public ResponseEntity<PrizeResponseDto> getPrize(
            @PathVariable final Long id
    ){
        return ResponseEntity.ok(prizeService.getPrize(id));
    }

    //경품 정보 수정 API (팩, 이름, 등급, 설명, 교환 조건, 처음 수량)
    @PatchMapping("/prize/{id}")
    public ResponseEntity<PrizeResponseDto> updatePrize(
            @PathVariable final Long id,
            @Validated(Update.class) @RequestBody final PrizeRequestDto prizeRequestDto
    ){
        return ResponseEntity.ok(prizeService.updatePrize(id, prizeRequestDto));
    }

    //경품 남은 수량 수정 API
    @PatchMapping("/prize/{id}/remaining")
    public ResponseEntity<PrizeResponseDto> updateRemaining(
            @PathVariable final Long id,
            @Validated @RequestBody final PrizeRemainingRequestDto prizeRemainingRequestDto
    ){
        return ResponseEntity.ok(prizeService.updateRemaining(id, prizeRemainingRequestDto));
    }

    //경품 남은 수량 증감 API
    @PatchMapping("/prize/{id}/adjust")
    public ResponseEntity<PrizeResponseDto> adjustRemaining(
            @PathVariable final Long id,
            @Validated @RequestBody final PrizeAdjustRequestDto prizeAdjustRequestDto
    ){
        return ResponseEntity.ok(prizeService.adjustRemaining(id, prizeAdjustRequestDto));
    }

    //경품 삭제 API
    @DeleteMapping("/prize/{id}")
    public ResponseEntity<Void> deletePrize(
            @PathVariable final Long id
    ){
        prizeService.deletePrize(id);

        return ResponseEntity.noContent().build();
    }
}
