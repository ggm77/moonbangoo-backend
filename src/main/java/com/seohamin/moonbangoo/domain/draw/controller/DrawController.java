package com.seohamin.moonbangoo.domain.draw.controller;

import com.seohamin.moonbangoo.domain.draw.dto.DrawCardResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawConfirmRequestDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawResponseDto;
import com.seohamin.moonbangoo.domain.draw.service.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    //카드 뽑기 API, 선택한 팩 안에서 경품 카드 5장을 줌 (수량 차감 없음)
    @PostMapping("/packs/{packId}/draw")
    public ResponseEntity<DrawResponseDto> draw(
            @PathVariable final Long packId
    ){
        return ResponseEntity.ok(drawService.draw(packId));
    }

    //뽑은 카드 중 가져갈 경품 확정 API (고른 경품만 수량 1 차감)
    @PostMapping("/draws/{drawId}/confirm")
    public ResponseEntity<DrawCardResponseDto> confirm(
            @PathVariable final Long drawId,
            @Validated @RequestBody final DrawConfirmRequestDto drawConfirmRequestDto
    ){
        return ResponseEntity.ok(drawService.confirm(drawId, drawConfirmRequestDto.getPrizeId()));
    }
}
