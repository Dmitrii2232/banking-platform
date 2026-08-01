// banking-resource-server/src/main/java/com/bank/resource/controller/ProductController.java
package com.bank.resource.controller;

import com.bank.resource.service.ProductGrpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductGrpcService productService;

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<?> getClientProducts(@AuthenticationPrincipal Jwt jwt) {
        String clientId = jwt.getClaimAsString("clientId");
        return ResponseEntity.ok(productService.getClientProducts(clientId));
    }

    @GetMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<?> getProduct(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable String productId) {
        String clientId = jwt.getClaimAsString("clientId");
        Map<String, Object> product = productService.getProduct(productId);
        if (!clientId.equals(product.get("clientId"))) {
            return ResponseEntity.status(403).body(Map.of("error", "FORBIDDEN", "message", "Access denied"));
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> openProduct(@AuthenticationPrincipal Jwt jwt,
                                          @RequestBody Map<String, Object> request) {
        String clientId = jwt.getClaimAsString("clientId");
        return ResponseEntity.ok(productService.openProduct(clientId, request));
    }

    @PostMapping("/products/{productId}/close")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> closeProduct(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable String productId,
                                           @RequestBody Map<String, String> request) {
        String clientId = jwt.getClaimAsString("clientId");
        Map<String, Object> product = productService.getProduct(productId);
        if (!clientId.equals(product.get("clientId"))) {
            return ResponseEntity.status(403).body(Map.of("error", "FORBIDDEN"));
        }
        String reason = request.getOrDefault("reason", "Client request");
        return ResponseEntity.ok(productService.closeProduct(productId, reason));
    }

    // ДОБАВЛЕНО: Поиск клиента по телефону
    @GetMapping("/clients/search")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<?> findClientByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(productService.findClientByPhone(phone));
    }

    // ДОБАВЛЕНО: Получение мастер-счета
    @GetMapping("/master-account")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<?> getMasterAccount(@AuthenticationPrincipal Jwt jwt) {
        String clientId = jwt.getClaimAsString("clientId");
        return ResponseEntity.ok(productService.getMasterAccount(clientId));
    }

    // ДОБАВЛЕНО: Смена мастер-счета
    @PostMapping("/master-account")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ResponseEntity<?> setMasterAccount(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody Map<String, String> request) {
        String clientId = jwt.getClaimAsString("clientId");
        String productId = request.get("productId");
        return ResponseEntity.ok(productService.setMasterAccount(clientId, productId));
    }
}