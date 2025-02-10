package com.eazybytes.filter;

import jakarta.servlet.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * 모든 요청을 처리하기 전에 인증 검증 진행 중임을 로그로 기록하는 필터입니다.
 */
public class AuthoritiesLoggingAtFilter implements Filter {

    // 로거 객체 생성: 이 필터의 로그 메시지를 기록하기 위한 로거입니다.
    private final Logger LOG =
            Logger.getLogger(AuthoritiesLoggingAtFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 인증 검증이 진행 중임을 로그로 기록합니다.
        LOG.info("Authentication Validation is in progress");

        // 요청 및 응답을 다음 필터 혹은 서블릿으로 전달합니다.
        chain.doFilter(request, response);
    }

}
