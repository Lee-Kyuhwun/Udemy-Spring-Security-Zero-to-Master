package com.eazybytes.config;

import com.eazybytes.filter.AuthoritiesLoggingAfterFilter;
import com.eazybytes.filter.AuthoritiesLoggingAtFilter;
import com.eazybytes.filter.CsrfCookieFilter;
import com.eazybytes.filter.RequestValidationBeforeFilter;
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

import java.util.Arrays;
import java.util.Collections;@Configuration  // 이 어노테이션은 해당 클래스가 스프링 설정 정보를 담고 있음을 나타냅니다.
public class ProjectSecurityConfig {

    // 이 메서드는 기본 보안 필터 체인을 구성하고 반환합니다.
    @Bean  // @Bean 어노테이션은 메서드의 반환 값을 스프링 컨테이너에 빈으로 등록하게 됩니다.
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 토큰과 관련된 속성 이름을 설정하기 위한 핸들러 생성
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
//                // 보안 컨텍스트 설정: 명시적인 저장이 필요하지 않습니다.
//                .securityContext((context) -> context.requireExplicitSave(false))
                // 세션 관리: 세션은 항상 생성됩니다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS (Cross-Origin Resource Sharing) 설정: 클라이언트에서 다른 도메인의 리소스에 접근을 허용하기 위한 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() { // CORS 설정의 구체적인 내용을 정의하는 곳
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration(); // CORS 설정을 위한 객체를 새로 생성합니다.
                                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // 허용할 도메인을 설정합니다.
                                config.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메소드에 대한 CORS 요청을 허용합니다.
                                config.setAllowCredentials(true); //브라우저가 응답과 함께 쿠키나 HTTP 인증 정보를 전송하도록 허용합니다
                                config.setAllowedHeaders(Arrays.asList("Authorization")); // 허용할 헤더를 설정합니다. 
                        config.setMaxAge(3600L);  // 브라우저가 CORS 프리플라이트 응답을 캐싱하는 시간 설정
                        return config;
                    }
                }))
                // CSRF 설정: 사이트 간 요청 위조를 방지하기 위한 설정
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/contact","/register","/notices") // 해당 경로는 CSRF 검사를 무시합니다.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))  // CSRF 토큰을 쿠키에 저장합니다.
                // 새로운 필터 추가: CSRF 쿠키를 처리하는 필터
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                // 각 경로에 대한 접근 권한 설정
                .authorizeHttpRequests((requests)->requests
                        .requestMatchers("/myAccount").hasRole("USER")
                        .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/myLoans").hasRole("USER")
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers("/user").authenticated()  // 인증된 사용자만 허용
                        .requestMatchers("/notices","/contact","/register").permitAll())  // 모든 사용자에게 허용
                .formLogin(Customizer.withDefaults())  // 로그인 폼을 사용하여 사용자 인증을 진행합니다.
                .httpBasic(Customizer.withDefaults());  // HTTP Basic 인증 방식을 사용합니다.

        return http.build();  // 구성된 보안 설정을 적용하고 필터 체인을 반환합니다.
    }

    // 암호 인코딩을 위한 빈을 제공합니다. 여기서는 BCrypt 암호화 알고리즘을 사용합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
