package com.eazybytes.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {

    /**
     * Spring Security 설정을 위한 빈(Bean) 정의입니다.
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 토큰을 요청 속성으로 설정하기 위한 핸들러를 초기화합니다.
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf"); // CSRF 토큰의 이름을 "_csrf"로 설정합니다.

        http
                // CORS(Cross-Origin Resource Sharing) 정책을 설정합니다.
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration(); // CORS 설정 객체 초기화
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // 허용된 출처 설정
                        config.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
                        config.setAllowCredentials(true); // 쿠키, HTTP 인증 등을 허용하도록 설정
                        config.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                        config.setMaxAge(3600L); // 사전 요청 결과 캐시 시간 설정
                        return config; // 설정된 CORS 객체 반환
                    }
                }))
                // CSRF (Cross-Site Request Forgery) 보호 설정
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(requestHandler) // 요청에서 CSRF 토큰을 처리할 핸들러 설정
                        .ignoringRequestMatchers("/contact","/register") // 이 URL 패턴에 대해서는 CSRF 검사를 무시
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 토큰을 쿠키에 저장하되, JavaScript에서 접근 가능하도록 설정
                )
                // 요청에 따른 권한 설정
                .authorizeHttpRequests((requests) ->
                        requests
                                // 인증된 사용자만 접근 가능한 URL 패턴 설정
                                .requestMatchers("/myAccount", "/myBalance","/myLoans","/myCards","/user").authenticated()
                                // 모든 사용자에게 접근을 허용하는 URL 패턴 설정
                                .requestMatchers("/notices","/contact","/register").permitAll()
                )
                // 폼 기반 로그인 설정. 여기서는 기본 설정을 사용합니다.
                .formLogin(Customizer.withDefaults())
                // HTTP 기본 인증 설정. 기본 설정을 사용합니다.
                .httpBasic(Customizer.withDefaults());

        // 위의 모든 설정을 바탕으로 SecurityFilterChain 객체를 생성하여 반환합니다.
        return http.build();
    }


    /*
    * NoOpPasswordEncoder은 Spring Security 5.0 이전 버전에서 사용되던 인코더입니다.
    * 비밀번호가 텍스트로 저장되기 때문에 보안에 취약합니다.
    * */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
