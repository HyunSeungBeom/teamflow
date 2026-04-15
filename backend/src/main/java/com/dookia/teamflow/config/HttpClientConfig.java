package com.dookia.teamflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 외부 HTTP 호출용 공용 클라이언트 빈.
 * RestClient 는 thread-safe 하므로 싱글톤으로 관리하고, 호출 측은 주입받아 재사용한다.
 * (개별 서비스에서 RestClient.create() 로 신규 인스턴스를 만들지 않는다.)
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
