package com.dookia.teamflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS 허용 origin 설정. application.yml 의 app.cors.* 와 매핑된다.
 * 여러 환경(local/dev/prod) 에서 프론트 도메인이 다르므로 프로파일별로 override 한다.
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
    List<String> allowedOrigins
) {
}
