package com.seohamin.moonbangoo.domain.user.controller;

import com.seohamin.moonbangoo.domain.user.dto.UserResponseDto;
import com.seohamin.moonbangoo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //내 정보 조회 API
    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDto> getMe(
            @AuthenticationPrincipal final String userIdStr
    ){

        final Long userId = Long.parseLong(userIdStr);

        return ResponseEntity.ok(userService.getUser(userId));
    }
}
