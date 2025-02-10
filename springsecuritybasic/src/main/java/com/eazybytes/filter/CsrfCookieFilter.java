package com.eazybytes.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName()); // CSRF 토큰을 요청 속성에서 추출합니다.
        if(null!=csrfToken.getHeaderName()){ // CSRF 토큰이 존재하는 경우
            response.setHeader(csrfToken.getHeaderName(),csrfToken.getToken()); // 헤더에 CSRF 토큰을 설정합니다.
        }
        filterChain.doFilter(request,response);// 다음 필터로 요청을 전달합니다.
    }
}
