package com.dookia.teamflow.auth.oauth;

/**
 * OAuth 제공자에서 받아오는 사용자 정보의 공통 형태.
 * 각 OAuthProvider 구현체가 이 형태로 정규화해서 반환한다.
 */
public record OAuthUserInfo(
    String providerId,
    String email,
    String name,
    String picture
) {
}
