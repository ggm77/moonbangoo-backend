package com.seohamin.moonbangoo.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 어드민 로그인 요청 DTO
 */
@Getter
public class AdminLoginRequestDto {

    @NotBlank
    private String password;
}
