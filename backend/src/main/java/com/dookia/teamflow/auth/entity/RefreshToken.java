package com.dookia.teamflow.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * REFRESH_TOKEN 값 객체. Redis 에 JSON 으로 직렬화되어 저장된다.
 * TTL 은 엔티티 속성이 아니라 저장 시점의 정책 파라미터이므로 Repository.save() 호출 시 Duration 으로 전달한다.
 *
 * <ul>
 *   <li>token key    : {@code refresh:token:{tokenHash}}  — String, TTL=저장 시 전달된 Duration</li>
 *   <li>family index : {@code refresh:family:{familyId}} — Redis Set, 멤버={tokenHash}, 동일 TTL</li>
 * </ul>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RefreshToken {

    private String tokenHash;
    private Long userNo;
    private String familyId;
    private boolean used;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime expireDate;
    private LocalDateTime createDate;

    public void markUsed() {
        this.used = true;
    }

    /**
     * Jackson 이 isXxx() 를 boolean getter 로 인식해 JSON 에 "expired" 필드를 생성하는 것을 막는다.
     * 이 플래그는 저장 대상 상태가 아니라 expireDate 로부터 계산되는 파생값이다.
     */
    @JsonIgnore
    public boolean isExpired() {
        return expireDate != null && LocalDateTime.now().isAfter(expireDate);
    }

    /** @see #isExpired() — 파생값이라 직렬화에서 제외한다. */
    @JsonIgnore
    public boolean isValid() {
        return !used && !isExpired();
    }
}
