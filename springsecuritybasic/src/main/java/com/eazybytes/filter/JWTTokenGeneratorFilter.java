package com.eazybytes.filter;

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT 토큰을 생성하는 필터.
 * 요청이 들어올 때마다 한 번만 실행된다.
 */
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    /**
     * 요청이 들어올 때마다 JWT 토큰 생성 로직이 실행됩니다.
     * 현재 인증된 사용자가 있으면 JWT 토큰을 생성하고 응답 헤더에 추가합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 현재 보안 컨텍스트에서 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자가 있는 경우
        if (null != authentication) {
            // JWT 키 생성
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

            // JWT 토큰 생성
            String jwt = Jwts.builder()
                    .setIssuer("Eazy Bank")
                    .setSubject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 30000000))
                    .signWith(key)
                    .compact();

            // 생성된 JWT 토큰을 응답 헤더에 추가
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        // 다음 필터 체인으로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 해당 필터가 실행되어야 하는지를 결정합니다.
     * "/user" 경로의 요청만 필터가 실행됩니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user");
    }

    /**
     * 사용자의 권한을 문자열로 변환합니다.
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
}
