package com.seohamin.moonbangoo.domain.admin;

import com.seohamin.moonbangoo.domain.admin.interceptor.AdminAuthInterceptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * 어드민 API 테스트용 설정, 모든 요청을 로그인한 상태로 보냄
 * 어드민 API를 쓰는 테스트 클래스에 @Import로 붙여서 사용
 */
@TestConfiguration
public class AdminLoggedInConfig {

    @Bean
    MockMvcBuilderCustomizer adminLoggedInCustomizer() {
        return builder -> builder.defaultRequest(get("/").sessionAttr(AdminAuthInterceptor.SESSION_KEY, true));
    }
}
