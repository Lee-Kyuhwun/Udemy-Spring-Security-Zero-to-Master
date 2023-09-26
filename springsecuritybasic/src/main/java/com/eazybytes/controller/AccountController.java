package com.eazybytes.controller;


import com.eazybytes.model.Accounts;
import com.eazybytes.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
/**
 * AccountController는 계좌 관련 요청을 처리하는 REST 컨트롤러입니다.
 */
@RestController
public class AccountController {

    // AccountRepository 인스턴스를 위한 멤버 변수
    private final AccountRepository accountRepository;

    /**
     * AccountController의 생성자입니다.
     * Spring의 @Autowired 어노테이션을 통해 AccountRepository를 주입받습니다.
     *
     * @param accountRepository 계좌 정보에 접근하기 위한 JPA Repository
     */
    public AccountController(@Autowired AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 주어진 고객 ID에 해당하는 계좌 정보를 검색하는 엔드포인트입니다.
     *
     * @param id 고객 ID
     * @return 해당 고객 ID와 연관된 계좌 정보. 계좌 정보가 없을 경우 null을 반환합니다.
     */
    @GetMapping("/myAccount")
    public Accounts getAccount(@PathVariable int id) {
        Accounts accounts = accountRepository.findByCustomerId(id);
        if(accounts != null){
            return accounts;
        }else{
            return null;
        }
    }
}
