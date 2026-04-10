package com.dookia.teamflow.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Google OAuth 클라이언트 설정값. application.yml의 app.google-oauth.* 와 매핑된다.
 */
@ConfigurationProperties(prefix = "app.google-oauth")
public record GoogleOAuthProperties(
    String clientId,
    String clientSecret,
    String tokenUri
) {
}
