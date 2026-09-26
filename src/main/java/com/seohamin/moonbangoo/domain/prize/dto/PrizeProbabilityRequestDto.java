package com.seohamin.moonbangoo.domain.prize.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 경품 확률 수정 요청 DTO
 */
@Getter
public class PrizeProbabilityRequestDto {

    //0 ~ 1, 소수점 6자리까지 (ex. 0.05 = 5%)
    @NotNull
    @DecimalMin("0")
    @DecimalMax("1")
    @Digits(integer = 1, fraction = 6)
    private BigDecimal probability;
}
