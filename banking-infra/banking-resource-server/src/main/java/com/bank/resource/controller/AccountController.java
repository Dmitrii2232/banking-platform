package com.bank.resource.controller;

import com.bank.resource.service.AccountingGrpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountingGrpcService accountingService;

    @GetMapping("/{accountCode}/balance")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<?> getBalance(@PathVariable String accountCode) {
        return ResponseEntity.ok(accountingService.getBalance(accountCode));
    }

    @GetMapping("/trial-balance")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<?> verifyTrialBalance() {
        return ResponseEntity.ok(accountingService.verifyBalance());
    }
}