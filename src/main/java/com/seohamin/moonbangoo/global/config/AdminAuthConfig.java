package com.seohamin.moonbangoo.global.config;

import com.seohamin.moonbangoo.domain.admin.interceptor.AdminAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 어드민 API에 로그인 검사를 붙임
 * 로그인/로그아웃 API는 로그인 전에도 호출할 수 있어야 하므로 제외
 */
@Configuration
@RequiredArgsConstructor
public class AdminAuthConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry){
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns("/api/v1/admin/login", "/api/v1/admin/logout");
    }
}
