package com.dookia.teamflow.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Auth 도메인의 ConfigurationProperties를 활성화한다.
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, GoogleOAuthProperties.class})
public class AuthConfig {
}
