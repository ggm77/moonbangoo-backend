package com.seohamin.moonbangoo.domain.draw.controller;

import com.seohamin.moonbangoo.domain.draw.dto.DrawRecordListResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.DrawRecordResponseDto;
import com.seohamin.moonbangoo.domain.draw.dto.ResetRequestDto;
import com.seohamin.moonbangoo.domain.draw.dto.SummaryResponseDto;
import com.seohamin.moonbangoo.domain.draw.service.DrawAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사장님이 당첨 기록과 이벤트 진행 상황을 관리하는 API
 * 인증 없음, 가게 내부망에서만 접속하는 것을 전제로 함
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DrawAdminController {

    private final DrawAdminService drawAdminService;

    //당첨 기록 조회 API (최근 100개)
    @GetMapping("/draws")
    public ResponseEntity<DrawRecordListResponseDto> getRecords(){
        return ResponseEntity.ok(drawAdminService.getRecords());
    }

    //당첨 기록 되돌리기 API (경품 수량 1 복원)
    @PostMapping("/draw/{id}/cancel")
    public ResponseEntity<DrawRecordResponseDto> cancel(
            @PathVariable final Long id
    ){
        return ResponseEntity.ok(drawAdminService.cancel(id));
    }

    //남은 수량 요약 API
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponseDto> getSummary(){
        return ResponseEntity.ok(drawAdminService.getSummary());
    }

    //이벤트 초기화 API (남은 수량 복원, 기록 삭제)
    @PostMapping("/reset")
    public ResponseEntity<Void> reset(
            @Validated @RequestBody final ResetRequestDto resetRequestDto
    ){
        drawAdminService.reset();

        return ResponseEntity.noContent().build();
    }
}
