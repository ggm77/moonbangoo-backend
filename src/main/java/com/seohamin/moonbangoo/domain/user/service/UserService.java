package com.seohamin.moonbangoo.domain.user.service;

import com.seohamin.moonbangoo.domain.user.dto.UserResponseDto;
import com.seohamin.moonbangoo.domain.user.entity.User;
import com.seohamin.moonbangoo.domain.user.repository.UserRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저의 정보를 조회하는 메서드
     * @param userId 조회할 유저 아이디
     * @return 유저 DTO
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUser(final Long userId){
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return new UserResponseDto(user);
    }
}
