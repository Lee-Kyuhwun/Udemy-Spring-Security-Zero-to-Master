package com.easybytes.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController { // 최종 사용자의 거래 목록을 반환


    @GetMapping("/myBalance")
    public String getBalance() {
        return "Here are the balance details from the DB";
    }

}
