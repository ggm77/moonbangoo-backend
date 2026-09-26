package com.seohamin.moonbangoo.support;

import com.seohamin.moonbangoo.domain.user.dto.oauth.UserOauth2AccountsRequestDto;
import com.seohamin.moonbangoo.domain.user.entity.Role;
import com.seohamin.moonbangoo.domain.user.entity.User;
import com.seohamin.moonbangoo.domain.user.repository.UserRepository;
import com.seohamin.moonbangoo.global.auth.jwt.JwtProvider;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 테스트에서 가입된 유저와 액세스 토큰을 만들기 위한 헬퍼
 */
@Component
public class TestAuthHelper {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public TestAuthHelper(final UserRepository userRepository, final JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    //일반 유저 생성
    public User createUser(final String nickname) {
        final User user = new User(UserOauth2AccountsRequestDto.builder()
                .provider("kakao")
                .providerUserId(nickname)
                .nickname(nickname)
                .build());
        return userRepository.save(user);
    }

    //사장님(ADMIN) 유저 생성, 실제로는 DB에서 role을 바꿈
    public User createAdmin(final String nickname) {
        final User user = new User(UserOauth2AccountsRequestDto.builder()
                .provider("kakao")
                .providerUserId(nickname)
                .nickname(nickname)
                .build());
        ReflectionTestUtils.setField(user, "role", Role.ADMIN);
        return userRepository.save(user);
    }

    //유저의 Authorization 헤더 값
    public String bearer(final User user) {
        return "Bearer " + jwtProvider.createAccessToken(user.getId(), user.getRole().getKey());
    }
}
