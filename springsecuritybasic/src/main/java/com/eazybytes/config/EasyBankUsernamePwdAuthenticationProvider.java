package com.eazybytes.config;


import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class EasyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public EasyBankUsernamePwdAuthenticationProvider(@Autowired PasswordEncoder passwordEncoder, @Autowired CustomerRepository customerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }




    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 사용자로부터 입력받은 사용자 이름(username) 및 비밀번호(pwd)를 추출합니다.
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        // 데이터베이스에서 주어진 사용자 이름(이메일)으로 고객 정보를 조회합니다.
        List<Customer> customers = customerRepository.findByEmail(username);

        // 검색 결과로 반환된 고객 정보가 존재하는 경우
        if(customers.size() > 0) {
            // 입력받은 비밀번호와 데이터베이스에 저장된 비밀번호가 일치하는지 검사합니다.
            if(passwordEncoder.matches(pwd, customers.get(0).getPwd())){

                // 고객의 역할(role)을 기반으로 권한 정보를 생성합니다.
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(customers.get(0).getRole()));

                // 인증이 성공하면 UsernamePasswordAuthenticationToken 객체를 생성하고 반환합니다.
                return new UsernamePasswordAuthenticationToken(username, pwd, authorities);

            } else {
                // 비밀번호가 일치하지 않을 경우 예외를 발생시킵니다.
                throw new RuntimeException("User not found : " + username);
            }
        } else {
            // 조회된 고객 정보가 없는 경우 예외를 발생시킵니다.
            throw new BadCredentialsException("No User registered with this details");
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
