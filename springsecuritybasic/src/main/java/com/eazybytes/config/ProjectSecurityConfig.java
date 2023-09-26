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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {


    // 빈(Bean) 설정을 통해 Spring Security 필터 체인을 정의합니다.
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                return config;
            }
        }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) ->
                        // "/myAccount", "/myBalance","/myLoans", "/myCards" URL에 대한 요청은 인증된 사용자만 허용합니다.
                        requests.requestMatchers("/myAccount", "/myBalance","/myLoans","/myCards","/user").authenticated()
                                // "/notices"와 "/contact" URL에 대한 요청은 모든 사용자에게 허용합니다.
                                .requestMatchers("/notices","/contact","/register").permitAll())
                // 로그인 폼을 사용한 인증 방식을 설정합니다. Customizer.withDefaults()는 기본 설정을 사용함을 의미합니다.
                .formLogin(Customizer.withDefaults())
                // HTTP Basic 인증 방식을 설정합니다. Customizer.withDefaults()는 기본 설정을 사용함을 의미합니다.
                .httpBasic(Customizer.withDefaults());
        // 설정된 보안 설정을 바탕으로 SecurityFilterChain 객체를 빌드하여 반환합니다.
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
