package com.bank.domain.accounting;

import com.bank.domain.common.Money;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralLedger {
    
    private final Map<String, AccountBalance> balances = new ConcurrentHashMap<>();
    
    public void post(List<AccountingEntry> entries) {
        for (AccountingEntry entry : entries) {
            updateAccount(entry.getDebitAccount(), entry.getAmount(), true);
            updateAccount(entry.getCreditAccount(), entry.getAmount(), false);
        }
    }
    
    private void updateAccount(AccountType accountType, Money amount, boolean isDebit) {
        String key = accountType.getCode();
        AccountBalance balance = balances.computeIfAbsent(key,
            k -> new AccountBalance(accountType, Money.ZERO_RUB));
        
        if (isDebit) {
            if (accountType.getSide() == Side.ACTIVE) {
                balance.addDebit(amount);
            } else {
                balance.subtractCredit(amount);
            }
        } else {
            if (accountType.getSide() == Side.ACTIVE) {
                balance.subtractCredit(amount);
            } else {
                balance.addDebit(amount);
            }
        }
    }
    
    public Money getBalance(AccountType accountType) {
        AccountBalance balance = balances.get(accountType.getCode());
        return balance != null ? balance.getCurrentBalance() : Money.ZERO_RUB;
    }
    
    public boolean verifyTrialBalance() {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (AccountBalance balance : balances.values()) {
            if (balance.getAccountType().getSide() == Side.ACTIVE) {
                totalDebit = totalDebit.add(balance.getCurrentBalance().getAmount());
            } else {
                totalCredit = totalCredit.add(balance.getCurrentBalance().getAmount());
            }
        }
        
        return totalDebit.compareTo(totalCredit) == 0;
    }
    
    private static class AccountBalance {
        private final AccountType accountType;
        private Money balance;
        
        AccountBalance(AccountType accountType, Money initialBalance) {
            this.accountType = accountType;
            this.balance = initialBalance;
        }
        
        void addDebit(Money amount) { this.balance = this.balance.add(amount); }
        void subtractCredit(Money amount) { this.balance = this.balance.subtract(amount); }
        Money getCurrentBalance() { return balance; }
        AccountType getAccountType() { return accountType; }
    }
}