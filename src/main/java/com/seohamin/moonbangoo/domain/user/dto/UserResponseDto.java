package com.seohamin.moonbangoo.domain.user.dto;

import com.seohamin.moonbangoo.domain.user.entity.Role;
import com.seohamin.moonbangoo.domain.user.entity.User;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String nickname;
    private final String profileImage;
    private final Role role;
    private final Instant createdAt;

    public UserResponseDto(final User user){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
