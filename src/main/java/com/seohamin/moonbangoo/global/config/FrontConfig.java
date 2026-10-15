package com.seohamin.moonbangoo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * 손님이 보는 프론트엔드(게임 메뉴, 카드뽑기)를 같은 서버에서 서빙
 * 빌드 결과물(dist)이 /moonbangoo/ 주소 기준으로 만들어져 있으므로 같은 경로로 열어줌
 * 접속 주소는 http://서버주소:포트/moonbangoo/
 * 프론트를 다시 빌드하면 src/main/resources/front 안의 파일을 새 dist 내용으로 교체
 */
@Configuration
public class FrontConfig implements WebMvcConfigurer {

    private static final String FRONT_LOCATION = "classpath:/front/";

    //주소 끝의 슬래시 유무와 관계없이 메뉴 화면을 열어줌
    @Override
    public void addViewControllers(final ViewControllerRegistry registry){
        registry.addViewController("/moonbangoo").setViewName("forward:/moonbangoo/index.html");
        registry.addViewController("/moonbangoo/").setViewName("forward:/moonbangoo/index.html");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry){
        //html은 새로 빌드하면 바로 반영되도록 매번 확인
        registry.addResourceHandler("/moonbangoo/**")
                .addResourceLocations(FRONT_LOCATION)
                .setCacheControl(CacheControl.noCache());

        //assets 파일 이름에는 내용 해시가 붙어 있어 내용이 바뀌면 이름도 바뀌므로 오래 캐시해도 됨
        registry.addResourceHandler("/moonbangoo/assets/**")
                .addResourceLocations(FRONT_LOCATION + "assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }
}
