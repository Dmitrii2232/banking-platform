package com.bank.domain.exception;

import com.bank.domain.common.Money;

public class AmlLimitExceededException extends BankingException {
    
    private final String clientId;
    private final Money limit;
    private final Money attempted;
    
    public AmlLimitExceededException(String clientId, Money limit, Money attempted) {
        super("AML_LIMIT_EXCEEDED",
            String.format("Превышен лимит по 115-ФЗ: client=%s, лимит=%s, попытка=%s",
                clientId, limit, attempted));
        this.clientId = clientId;
        this.limit = limit;
        this.attempted = attempted;
    }
    
    public String getClientId() { return clientId; }
    public Money getLimit() { return limit; }
    public Money getAttempted() { return attempted; }
}