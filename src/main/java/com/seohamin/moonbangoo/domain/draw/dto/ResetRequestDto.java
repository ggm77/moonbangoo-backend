package com.seohamin.moonbangoo.domain.draw.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

/**
 * 이벤트 초기화 요청 DTO
 * 되돌릴 수 없는 작업이라 실수로 호출하지 않도록 확인 값을 요구함
 */
@Getter
public class ResetRequestDto {

    @AssertTrue
    private boolean confirm;
}
