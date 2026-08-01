package com.bank.domain.exception;

import com.bank.domain.common.Money;

public class InsufficientFundsException extends BankingException {
    
    private final Money requested;
    private final Money available;
    
    public InsufficientFundsException(String message) {
        super("INSUFFICIENT_FUNDS", message);
        this.requested = null;
        this.available = null;
    }
    
    public InsufficientFundsException(Money requested, Money available) {
        super("INSUFFICIENT_FUNDS",
            String.format("Недостаточно средств: запрошено %s, доступно %s", requested, available));
        this.requested = requested;
        this.available = available;
    }
    
    public Money getRequested() { return requested; }
    public Money getAvailable() { return available; }
    public Money getShortfall() {
        return requested != null && available != null ? requested.subtract(available) : null;
    }
}