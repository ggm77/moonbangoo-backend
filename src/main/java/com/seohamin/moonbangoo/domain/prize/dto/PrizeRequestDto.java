package com.seohamin.moonbangoo.domain.prize.dto;

import com.seohamin.moonbangoo.global.validation.Create;
import com.seohamin.moonbangoo.global.validation.Update;
import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * 경품 등록(Create), 수정(Update) 요청 DTO
 * 수정은 변경할 값만 보내면 됨
 * 수정시 description, condition에 빈 문자열을 보내면 값이 지워짐
 * 남은 수량은 수정 API가 따로 있음
 */
@Getter
public class PrizeRequestDto {

    //경품이 들어갈 팩, 수정시 보내면 다른 팩으로 이동
    @NotNull(groups = Create.class)
    private Long packId;

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = {Create.class, Update.class})
    private String name;

    //C, R, SR, UR
    @NotBlank(groups = Create.class)
    private String rarity;

    @Size(max = 500, groups = {Create.class, Update.class})
    private String description;

    @Size(max = 200, groups = {Create.class, Update.class})
    private String condition;

    //처음 수량, 등록시 남은 수량도 같은 값으로 시작
    @NotNull(groups = Create.class)
    @PositiveOrZero(groups = {Create.class, Update.class})
    private Integer total;
}
