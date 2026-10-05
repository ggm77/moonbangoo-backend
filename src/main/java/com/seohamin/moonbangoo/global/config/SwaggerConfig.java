package com.seohamin.moonbangoo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(apiInfo());
    }

    public Info apiInfo() {
        return new Info()
                .title("Moonbangoo")
                .description("어른문방구 카드팩 뽑기 API")
                .version(buildProperties.getVersion());
    }
}
