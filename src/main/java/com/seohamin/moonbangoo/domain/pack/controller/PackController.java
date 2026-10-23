package com.seohamin.moonbangoo.domain.pack.controller;

import com.seohamin.moonbangoo.domain.pack.dto.PackItemListResponseDto;
import com.seohamin.moonbangoo.domain.pack.service.PackService;
import com.seohamin.moonbangoo.domain.prize.dto.PrizeItemListResponseDto;
import com.seohamin.moonbangoo.domain.prize.service.PrizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PackController {

    private final PackService packService;
    private final PrizeService prizeService;

    //손님이 고를 팩 목록 API, 활성 팩만 주고 품절 여부를 같이 알려줌
    @GetMapping("/packs")
    public ResponseEntity<PackItemListResponseDto> getPacks(){
        return ResponseEntity.ok(packService.getAvailablePacks());
    }

    //손님이 보는 팩의 경품 목록 API, 품절 경품도 남은 수량/전체 수량과 함께 줌
    @GetMapping("/packs/{packId}/prizes")
    public ResponseEntity<PrizeItemListResponseDto> getPrizes(
            @PathVariable final Long packId
    ){
        return ResponseEntity.ok(prizeService.getPrizeItems(packId));
    }
}
