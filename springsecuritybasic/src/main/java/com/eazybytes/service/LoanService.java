package com.eazybytes.service;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    @PreAuthorize("hasRole('VIEWLOANS')")
    public String getLoanDetails() {
        return "Loan Details";
    }
}
