package com.seohamin.moonbangoo.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Configuration
public class UploadConfig implements WebMvcConfigurer {

    // 업로드한 이미지를 저장하는 폴더 (application.yaml에서 설정)
    @Value("${upload.dir}")
    private String uploadDir;

    //저장한 이미지를 /uploads/** 로 보여줌, 파일 이름이 매번 새로 만들어지므로 오래 캐시해도 됨
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry){
        final String location = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }
}
