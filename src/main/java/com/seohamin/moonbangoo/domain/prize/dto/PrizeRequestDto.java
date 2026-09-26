package com.seohamin.moonbangoo.domain.prize.dto;

import com.seohamin.moonbangoo.global.validation.Create;
import com.seohamin.moonbangoo.global.validation.Update;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 경품 등록(Create), 수정(Update) 요청 DTO
 * 수정은 변경할 값만 보내면 됨
 * 수정시 image, description, condition에 빈 문자열을 보내면 값이 지워짐
 * 확률과 재고는 수정 API가 따로 있음
 */
@Getter
public class PrizeRequestDto {

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = {Create.class, Update.class})
    private String name;

    //C, R, SR, UR
    @NotBlank(groups = Create.class)
    private String rarity;

    @NotBlank(groups = Create.class)
    @Size(max = 30, groups = {Create.class, Update.class})
    private String category;

    @Size(max = 2048, groups = {Create.class, Update.class})
    private String image;

    @Size(max = 500, groups = {Create.class, Update.class})
    private String description;

    @Size(max = 200, groups = {Create.class, Update.class})
    private String condition;

    //등록시에만 사용 (0 ~ 1, 소수점 6자리까지)
    @NotNull(groups = Create.class)
    @DecimalMin(value = "0", groups = Create.class)
    @DecimalMax(value = "1", groups = Create.class)
    @Digits(integer = 1, fraction = 6, groups = Create.class)
    private BigDecimal probability;

    //등록시에만 사용 (null이면 무제한)
    @PositiveOrZero(groups = Create.class)
    private Integer stock;
}
