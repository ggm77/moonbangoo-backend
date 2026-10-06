package com.seohamin.moonbangoo.domain.draw.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 뽑은 카드 중 가져갈 경품 선택 요청 DTO
 */
@Getter
public class DrawConfirmRequestDto {

    //뽑은 카드 5장에 있던 경품의 아이디
    @NotNull
    private Long prizeId;
}
