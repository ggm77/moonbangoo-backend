package com.seohamin.moonbangoo.domain.pack.controller;

import com.seohamin.moonbangoo.domain.pack.dto.PackItemListResponseDto;
import com.seohamin.moonbangoo.domain.pack.service.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PackController {

    private final PackService packService;

    //손님이 고를 팩 목록 API, 활성 팩만 주고 품절 여부를 같이 알려줌
    @GetMapping("/packs")
    public ResponseEntity<PackItemListResponseDto> getPacks(){
        return ResponseEntity.ok(packService.getAvailablePacks());
    }
}
