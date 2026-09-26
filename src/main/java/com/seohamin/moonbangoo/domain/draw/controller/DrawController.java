package com.seohamin.moonbangoo.domain.draw.controller;

import com.seohamin.moonbangoo.domain.draw.dto.DrawResponseDto;
import com.seohamin.moonbangoo.domain.draw.service.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    //카드 뽑기 API, 서로 다른 경품 카드 5장을 줌
    @PostMapping("/draw")
    public ResponseEntity<DrawResponseDto> draw(){
        return ResponseEntity.ok(drawService.draw());
    }
}
