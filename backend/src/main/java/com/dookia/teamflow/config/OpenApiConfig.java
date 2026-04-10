package com.dookia.teamflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi가 사용할 TeamFlow API 메타데이터.
 * /v3/api-docs 및 /swagger-ui.html 에서 이 정보가 노출된다.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI teamflowOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("TeamFlow API")
                .version("v1")
                .description("TeamFlow 백엔드 API. 인증은 JWT Bearer 토큰(Access Token)을 사용하며, "
                    + "Refresh Token은 httpOnly Cookie(teamflow_rt)로 관리된다."))
            .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
            .components(new Components()
                .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("로그인 후 응답으로 받은 accessToken 을 입력한다.")));
    }
}
