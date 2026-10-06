package com.seohamin.moonbangoo.domain.pack.dto;

import com.seohamin.moonbangoo.global.validation.Create;
import com.seohamin.moonbangoo.global.validation.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 팩 등록(Create), 수정(Update) 요청 DTO
 * 수정은 변경할 값만 보내면 됨
 * 수정시 image, icon에 빈 문자열을 보내면 값이 지워짐
 */
@Getter
public class PackRequestDto {

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = {Create.class, Update.class})
    private String name;

    @Size(max = 2048, groups = {Create.class, Update.class})
    private String image;

    @Size(max = 30, groups = {Create.class, Update.class})
    private String icon;

    //등록시 생략하면 true
    private Boolean active;
}
