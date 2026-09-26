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
 * 사장님(ADMIN)이 경품을 관리하는 API
 * 권한 검사는 SecurityConfig의 /api/v1/admin/** 에서 함
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

    //경품 목록 조회 API (확률 합 포함)
    @GetMapping("/prizes")
    public ResponseEntity<PrizeListResponseDto> getPrizes(){
        return ResponseEntity.ok(prizeService.getPrizes());
    }

    //경품 조회 API
    @GetMapping("/prize/{id}")
    public ResponseEntity<PrizeResponseDto> getPrize(
            @PathVariable final Long id
    ){
        return ResponseEntity.ok(prizeService.getPrize(id));
    }

    //경품 정보 수정 API (이름, 등급, 카테고리, 이미지, 설명, 교환 조건)
    @PatchMapping("/prize/{id}")
    public ResponseEntity<PrizeResponseDto> updatePrize(
            @PathVariable final Long id,
            @Validated(Update.class) @RequestBody final PrizeRequestDto prizeRequestDto
    ){
        return ResponseEntity.ok(prizeService.updatePrize(id, prizeRequestDto));
    }

    //경품 확률 수정 API
    @PatchMapping("/prize/{id}/probability")
    public ResponseEntity<PrizeResponseDto> updateProbability(
            @PathVariable final Long id,
            @Validated @RequestBody final PrizeProbabilityRequestDto prizeProbabilityRequestDto
    ){
        return ResponseEntity.ok(prizeService.updateProbability(id, prizeProbabilityRequestDto));
    }

    //경품 재고 수정 API
    @PatchMapping("/prize/{id}/stock")
    public ResponseEntity<PrizeResponseDto> updateStock(
            @PathVariable final Long id,
            @Validated @RequestBody final PrizeStockRequestDto prizeStockRequestDto
    ){
        return ResponseEntity.ok(prizeService.updateStock(id, prizeStockRequestDto));
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
