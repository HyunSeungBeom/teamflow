package com.dookia.teamflow.auth.oauth;

import com.dookia.teamflow.user.entity.UserProvider;

/**
 * OAuth 제공자(Google, Naver, Kakao …) 추상화.
 * Strategy 패턴 — AuthService 가 UserProvider 키로 적절한 구현을 라우팅한다.
 */
public interface OAuthProvider {

    /**
     * 이 구현체가 담당하는 제공자 식별자.
     */
    UserProvider provider();

    /**
     * Authorization Code 를 제공자 token endpoint 와 교환해 사용자 정보를 얻는다.
     *
     * @param code        프론트가 전달한 authorization code
     * @param redirectUri code 발급 시 사용한 redirect URI (제공자별 검증 필요)
     */
    OAuthUserInfo exchangeCodeForUser(String code, String redirectUri);
}
