package com.seohamin.moonbangoo.domain.admin.controller;

import com.seohamin.moonbangoo.domain.admin.dto.AdminLoginRequestDto;
import com.seohamin.moonbangoo.domain.admin.interceptor.AdminAuthInterceptor;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 어드민 로그인/로그아웃 API
 * 비밀번호는 application.yaml의 admin.password 한 개이고, 맞으면 세션에 로그인 표시를 남김
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminAuthController {

    private final byte[] password;

    public AdminAuthController(@Value("${admin.password}") final String password) {
        if (password.isBlank()) {
            throw new IllegalStateException("admin.password 가 비어 있습니다. application.yaml에 어드민 비밀번호를 설정하세요.");
        }
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    //로그인 API, 맞으면 204와 함께 로그인 쿠키를 내려줌
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid final AdminLoginRequestDto request,
            final HttpServletRequest httpRequest
    ){
        //응답 시간으로 비밀번호를 추측하지 못하게 시간이 일정한 비교를 사용
        final byte[] input = request.getPassword().getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(password, input)) {
            throw new CustomException(ExceptionCode.INVALID_ADMIN_PASSWORD);
        }

        //로그인 전에 만들어진 세션 번호를 그대로 쓰지 않도록 새 번호로 바꿈
        final HttpSession session = httpRequest.getSession();
        httpRequest.changeSessionId();
        session.setAttribute(AdminAuthInterceptor.SESSION_KEY, true);

        return ResponseEntity.noContent().build();
    }

    //로그아웃 API, 로그인하지 않았어도 204
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletRequest httpRequest){
        final HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }
}
