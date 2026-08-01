package com.bank.domain.exception;

public class ProductBlockedException extends BankingException {
    
    public ProductBlockedException(String message) {
        super("PRODUCT_BLOCKED", message);
    }
    
    public ProductBlockedException(String productId, String reason) {
        super("PRODUCT_BLOCKED",
            String.format("Продукт %s заблокирован: %s", productId, reason), reason);
    }
}