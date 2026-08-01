package com.bank.domain.exception;

import com.bank.domain.common.Money;

public class AccountingBalanceException extends BankingException {
    
    private final Money debitTotal;
    private final Money creditTotal;
    private final Money difference;
    
    public AccountingBalanceException(String message) {
        super("ACCOUNTING_BALANCE_ERROR", message);
        this.debitTotal = null;
        this.creditTotal = null;
        this.difference = null;
    }
    
    public AccountingBalanceException(Money debitTotal, Money creditTotal) {
        super("ACCOUNTING_BALANCE_ERROR",
            String.format("Нарушение двойной записи: Дебет=%s, Кредит=%s, Разница=%s",
                debitTotal, creditTotal, debitTotal.subtract(creditTotal)));
        this.debitTotal = debitTotal;
        this.creditTotal = creditTotal;
        this.difference = debitTotal.subtract(creditTotal);
    }
    
    public Money getDebitTotal() { return debitTotal; }
    public Money getCreditTotal() { return creditTotal; }
    public Money getDifference() { return difference; }
}