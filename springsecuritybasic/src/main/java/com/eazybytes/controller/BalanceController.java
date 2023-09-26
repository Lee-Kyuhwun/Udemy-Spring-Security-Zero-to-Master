package com.eazybytes.controller;


import com.eazybytes.model.AccountTransactions;
import com.eazybytes.repository.AccountTransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BalanceController는 사용자의 거래 내역에 관한 요청을 처리하는 REST 컨트롤러입니다.
 */
@RestController
public class BalanceController {

    // AccountTransactionsRepository 인스턴스를 위한 멤버 변수
    private final AccountTransactionsRepository accountTransactionsRepository;

    /**
     * BalanceController의 생성자입니다.
     * Spring의 @Autowired 어노테이션을 통해 AccountTransactionsRepository를 주입받습니다.
     *
     * @param accountTransactionsRepository 거래 내역에 접근하기 위한 JPA Repository
     */
    public BalanceController(@Autowired AccountTransactionsRepository accountTransactionsRepository) {
        this.accountTransactionsRepository = accountTransactionsRepository;
    }

    /**
     * 주어진 고객 ID에 해당하는 거래 내역 목록을 반환하는 엔드포인트입니다.
     * 거래 내역은 최신 거래부터 오래된 순으로 정렬됩니다.
     *
     * @param id 고객 ID
     * @return 해당 고객 ID와 연관된 거래 내역 목록. 거래 내역이 없을 경우 null을 반환합니다.
     */
    @GetMapping("/myBalance")
    public List<AccountTransactions> getBalance(@PathVariable int id) {
        List<AccountTransactions> accountTransactions = accountTransactionsRepository.findByCustomerIdOrderByTransactionDtDesc(id);
        if (accountTransactions != null ) {
            return accountTransactions;
        }else {
            return null;
        }
    }
}

