package com.eazybytes.config;

import com.eazybytes.model.Authority;
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
import java.util.Set;

// @Component 어노테이션은 Spring Framework에 이 클래스를 Bean으로 등록하라는 지시입니다.
// 따라서 Spring Context에서 이 클래스의 인스턴스를 생성하고 관리합니다.
@Component
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    // @Autowired 어노테이션은 Spring Framework에 이 필드를 자동 주입하도록 지시합니다.
    // 여기서는 CustomerRepository 타입의 빈을 자동 주입받습니다.
    @Autowired
    private CustomerRepository customerRepository;

    // PasswordEncoder를 주입받아, 비밀번호 암호화 및 비교를 담당합니다.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // authenticate 메서드는 사용자 인증을 담당합니다.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 인증 요청에서 제공된 사용자 이름과 비밀번호를 가져옵니다.
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        // 사용자 이름을 이용하여 데이터베이스에서 해당 사용자 정보를 검색합니다.
        List<Customer> customer = customerRepository.findByEmail(username);
        // 사용자 정보가 데이터베이스에 있는지 확인합니다.
        if (customer.size() > 0) {
            // 데이터베이스에 저장된 암호화된 비밀번호와 사용자에게서 받은 비밀번호를 비교합니다.
            if (passwordEncoder.matches(pwd, customer.get(0).getPwd())) {
                // 비밀번호가 일치하면 인증된 Authentication 객체를 반환합니다.
                return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(customer.get(0).getAuthorities()));
            } else {
                // 비밀번호가 일치하지 않으면 예외를 발생시킵니다.
                throw new BadCredentialsException("Invalid password!");
            }
        } else {
            // 사용자 정보가 없으면 예외를 발생시킵니다.
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    // 사용자에게 부여된 권한을 변환하여 GrantedAuthority 목록을 반환하는 private 메서드입니다.
    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    // 이 AuthenticationProvider가 지원하는 인증 타입을 확인하는 메서드입니다.
    // 여기서는 UsernamePasswordAuthenticationToken 타입의 인증만 지원합니다.
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
