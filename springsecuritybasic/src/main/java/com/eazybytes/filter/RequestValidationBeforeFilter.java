package com.eazybytes.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Basic 인증 토큰을 검증하는 필터입니다.
 */
public class RequestValidationBeforeFilter implements Filter {

    // Basic 인증 스키마 상수
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    // 인증 토큰의 문자셋 정의 (기본값: UTF-8)
    private Charset credentialsCharset = StandardCharsets.UTF_8;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 요청의 Authorization 헤더에서 인증 정보를 가져옵니다.
        String header = req.getHeader(AUTHORIZATION);

        // Authorization 헤더가 존재하는 경우
        if (header != null) {
            header = header.trim(); // 문자열 공백 제거

            // 해당 요청이 Basic 인증을 사용하는지 확인
            if (StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {

                // Basic 인증 토큰을 추출하고 Base64 디코딩을 진행
                byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                byte[] decoded;
                try {
                    decoded = Base64.getDecoder().decode(base64Token);
                    String token = new String(decoded, credentialsCharset);
                    int delim = token.indexOf(":");

                    // 유효하지 않은 토큰 처리 (":" 구분자가 없는 경우)
                    if (delim == -1) {
                        throw new BadCredentialsException("Invalid basic authentication token");
                    }

                    // 이메일 주소를 추출하고 "test" 문자열이 포함되어 있는지 확인
                    String email = token.substring(0, delim);
                    if (email.toLowerCase().contains("test")) {
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    // Base64 디코딩 실패 시 예외 처리
                    throw new BadCredentialsException("Failed to decode basic authentication token");
                }
            }
        }

        // 요청 및 응답을 다음 필터 혹은 서블릿으로 전달
        chain.doFilter(request, response);
    }
}
