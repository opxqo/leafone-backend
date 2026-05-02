package com.leafone.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "BearerAuth";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请输入 JWT Token（登录接口返回的 accessToken）");

        return new OpenAPI()
                .info(new Info()
                        .title("LeafOne 论坛 API")
                        .version("v1")
                        .description("校园论坛后端 API 文档，基于 Spring Boot 3 + Knife4j")
                        .contact(new Contact().name("LeafOne Team")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .schemaRequirement(schemeName, securityScheme);
    }
}
