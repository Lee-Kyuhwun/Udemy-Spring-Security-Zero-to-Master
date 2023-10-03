package com.eazybytes.config;

import com.eazybytes.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
@Configuration  // Spring의 설정 클래스임을 나타냅니다.
public class ProjectSecurityConfig {

    @Bean  // 스프링 빈으로 등록
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보안을 위한 핸들러 생성 및 설정
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .securityContext((context) -> context.requireExplicitSave(false))  // 명시적인 저장이 필요하지 않도록 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))  // 세션 생성 정책 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {  // CORS 설정
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));  // 허용된 출처 설정
                        config.setAllowedMethods(Collections.singletonList("*"));  // 모든 HTTP 메소드 허용
                        config.setAllowCredentials(true);  // 크리덴셜 허용
                        config.setAllowedHeaders(Collections.singletonList("*"));  // 모든 헤더 허용
                        config.setMaxAge(3600L);  // 캐시 최대 시간 설정
                        return config;
                    }
                }))
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/contact","/register","/notices")  // CSRF 검사에서 무시할 경로 설정
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))  // CSRF 토큰 저장소 설정
                .addFilterBefore(new CsrfCookieFilter(), CsrfFilter.class)// CSRF 쿠키 필터 추가
                .authorizeHttpRequests((requests)->requests  // 요청에 대한 인증 규칙 설정
                        .requestMatchers("/myAccount").hasRole("USER")  // "/myAccount"는 USER 역할이 필요합니다.
                        .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")  // "/myBalance"는 USER 또는 ADMIN 역할이 필요합니다.
                        .requestMatchers("/myLoans").hasRole("USER")  // "/myLoans"는 USER 역할이 필요합니다.
                        .requestMatchers("/myCards").hasRole("USER")  // "/myCards"는 USER 역할이 필요합니다.
                        .requestMatchers("/user").authenticated()  // "/user"는 인증된 사용자만 접근 가능합니다.
                        .requestMatchers("/notices","/contact","/register").permitAll())  // 모든 사용자가 접근 가능한 경로 설정
                .formLogin(Customizer.withDefaults())  // 기본 로그인 폼 사용
                .httpBasic(Customizer.withDefaults());  // 기본 HTTP Basic 인증 사용

        return http.build();
    }

    @Bean  // 스프링 빈으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위한 BCrypt 알고리즘 사용
    }
}
