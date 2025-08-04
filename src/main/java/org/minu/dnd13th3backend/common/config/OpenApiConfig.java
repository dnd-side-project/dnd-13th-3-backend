package org.minu.dnd13th3backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minu API")
                        .description("건강한 디지털 라이프를 위한 스크린타임 관리 및 생산성 향상 서비스")
                        .version("v1.0.0"));
    }
}