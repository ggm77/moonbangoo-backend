package com.seohamin.moonbangoo.domain.pack.controller;

import com.seohamin.moonbangoo.domain.pack.dto.PackListResponseDto;
import com.seohamin.moonbangoo.domain.pack.dto.PackRequestDto;
import com.seohamin.moonbangoo.domain.pack.dto.PackResponseDto;
import com.seohamin.moonbangoo.domain.pack.service.PackService;
import com.seohamin.moonbangoo.global.validation.Create;
import com.seohamin.moonbangoo.global.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사장님이 팩을 관리하는 API
 * 인증 없음, 가게 내부망에서만 접속하는 것을 전제로 함
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class PackAdminController {

    private final PackService packService;

    //팩 등록 API
    @PostMapping("/pack")
    public ResponseEntity<PackResponseDto> createPack(
            @Validated(Create.class) @RequestBody final PackRequestDto packRequestDto
    ){
        return ResponseEntity.ok(packService.createPack(packRequestDto));
    }

    //팩 목록 조회 API (비활성 팩 포함, 경품 수와 수량 합 포함)
    @GetMapping("/packs")
    public ResponseEntity<PackListResponseDto> getPacks(){
        return ResponseEntity.ok(packService.getPacks());
    }

    //팩 조회 API
    @GetMapping("/pack/{id}")
    public ResponseEntity<PackResponseDto> getPack(
            @PathVariable final Long id
    ){
        return ResponseEntity.ok(packService.getPack(id));
    }

    //팩 수정 API (이름, 이미지, 아이콘, 활성 여부)
    @PatchMapping("/pack/{id}")
    public ResponseEntity<PackResponseDto> updatePack(
            @PathVariable final Long id,
            @Validated(Update.class) @RequestBody final PackRequestDto packRequestDto
    ){
        return ResponseEntity.ok(packService.updatePack(id, packRequestDto));
    }

    //팩 삭제 API (경품이 남아있으면 삭제 불가)
    @DeleteMapping("/pack/{id}")
    public ResponseEntity<Void> deletePack(
            @PathVariable final Long id
    ){
        packService.deletePack(id);

        return ResponseEntity.noContent().build();
    }
}
