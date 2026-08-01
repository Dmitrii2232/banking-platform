package com.bank.resource.controller;

import com.bank.resource.service.TransactionGrpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionGrpcService transactionService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> deposit(@AuthenticationPrincipal Jwt jwt,
                                      @RequestBody Map<String, Object> request) {
        String clientId = jwt.getClaimAsString("clientId");
        String productId = (String) request.get("productId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = (String) request.getOrDefault("currency", "RUB");
        return ResponseEntity.ok(transactionService.deposit(productId, clientId, amount, currency));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody Map<String, Object> request) {
        String clientId = jwt.getClaimAsString("clientId");
        String productId = (String) request.get("productId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = (String) request.getOrDefault("currency", "RUB");
        return ResponseEntity.ok(transactionService.withdraw(productId, clientId, amount, currency));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> transfer(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody Map<String, Object> request) {
        String clientId = jwt.getClaimAsString("clientId");
        String sourceProductId = (String) request.get("sourceProductId");
        String destinationProductId = (String) request.get("destinationProductId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = (String) request.getOrDefault("currency", "RUB");
        return ResponseEntity.ok(transactionService.transfer(
            sourceProductId, destinationProductId, clientId, amount, currency));
    }
}