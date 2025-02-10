package com.eazybytes.filter;

import jakarta.servlet.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * 이 필터는 Spring Security의 `SecurityContextHolder`를 사용하여
 * 현재 인증된 사용자의 권한을 로깅하는 역할을 합니다.
 */
public class AuthoritiesLoggingAfterFilter implements Filter {

    // Logger 인스턴스 생성
    private final Logger LOG = Logger.getLogger(AuthoritiesLoggingAfterFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // SecurityContextHolder에서 현재 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 현재 인증된 사용자 세부 정보를 제공한다. (인증된 사용자의 이름, 비밀번호, 권한 등)

        // 인증 객체가 null이 아닌 경우, 사용자의 이름과 권한을 로깅
        if (null != authentication) {
            LOG.info("User " + authentication.getName()
                    + " is successfully authenticated and "
                    + "has the authorities " + authentication.getAuthorities().toString());
        }

        // 요청 및 응답을 다음 필터 또는 서블릿으로 전달
        chain.doFilter(request, response);
    }
}
