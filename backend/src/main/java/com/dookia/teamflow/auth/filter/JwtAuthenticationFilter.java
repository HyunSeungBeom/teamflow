package com.dookia.teamflow.auth.filter;

import com.dookia.teamflow.exception.AuthErrorCode;
import com.dookia.teamflow.exception.AuthException;
import com.dookia.teamflow.auth.service.JwtService;
import com.dookia.teamflow.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Authorization 헤더의 Bearer JWT를 검증해 SecurityContext에 사용자 번호(user.no)를 주입한다.
 * 토큰이 존재하지만 만료/무효 상태면 즉시 ApiResponse envelope 으로 401 응답 후 체인을 중단한다.
 * Authorization 헤더가 없거나 Bearer 스킴이 아니면 체인을 그대로 통과시켜 SecurityConfig 의 authorizeHttpRequests 가 401 처리하도록 한다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();
            try {
                Long userNo = jwtService.parseUserNo(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userNo, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthException e) {
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, e.getErrorCode());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(errorCode, errorCode.getMessage()));
    }
}
