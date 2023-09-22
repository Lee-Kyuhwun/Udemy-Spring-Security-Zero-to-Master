package com.easybytes.config;


import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {


    // 빈(Bean) 설정을 통해 Spring Security 필터 체인을 정의합니다.
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // HttpSecurity를 사용하여 HTTP 요청에 대한 보안 설정을 정의합니다.
        http.authorizeHttpRequests((requests) ->
                        // "/myAccount", "/myBalance","/myLoans", "/myCards" URL에 대한 요청은 인증된 사용자만 허용합니다.
                        requests.requestMatchers("/myAccount", "/myBalance","/myLoans","/myCards").authenticated()
                                // "/notices"와 "/contact" URL에 대한 요청은 모든 사용자에게 허용합니다.
                                .requestMatchers("/notice","/contact").permitAll())
                // 로그인 폼을 사용한 인증 방식을 설정합니다. Customizer.withDefaults()는 기본 설정을 사용함을 의미합니다.
                .formLogin(Customizer.withDefaults())
                // HTTP Basic 인증 방식을 설정합니다. Customizer.withDefaults()는 기본 설정을 사용함을 의미합니다.
                .httpBasic(Customizer.withDefaults());

        // 설정된 보안 설정을 바탕으로 SecurityFilterChain 객체를 빌드하여 반환합니다.
        return http.build();

        /**
         *  Configuration to deny all the requests
         */
        /*http.authorizeHttpRequests(requests -> requests.anyRequest().denyAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();*/

/*        *//**
         *  Configuration to permit all the requests
         *//*
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();*/
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(){

        // type 1
/*        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .authorities("admin")
                .build();
        
        
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("12345")
                .authorities("read")
                .build();*/

        // type 2
        UserDetails admin = User.withUsername("admin")
                .password("12345")
                .authorities("admin")
                .build();


        UserDetails user = User.withUsername("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }


    /*
    * NoOpPasswordEncoder은 Spring Security 5.0 이전 버전에서 사용되던 인코더입니다.
    * 비밀번호가 텍스트로 저장되기 때문에 보안에 취약합니다.
    * */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
