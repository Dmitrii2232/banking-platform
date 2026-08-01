package com.bank.domain.accounting;

import com.bank.domain.event.CashDepositedEvent;
import com.bank.domain.event.CashWithdrawnEvent;
import com.bank.domain.event.Event;
import com.bank.domain.event.FeeChargedEvent;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.LoanPaymentEvent;
import com.bank.domain.event.ProductOpenedEvent;
import com.bank.domain.exception.AccountingBalanceException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccountingEngine {
    
    private final Map<Class<? extends Event>, BookingRule> rules = new LinkedHashMap<>();
    
    public AccountingEngine() {
        registerRules();
    }
    
    private void registerRules() {
        rules.put(CashDepositedEvent.class, event -> {
            CashDepositedEvent e = (CashDepositedEvent) event;
            return List.of(new AccountingEntry(
                AccountType.CASH_DESK,
                AccountType.CLIENT_DEPOSIT,
                e.getAmount(),
                "Пополнение наличными",
                e.getEventId().getUuid().toString()
            ));
        });
        
        rules.put(CashWithdrawnEvent.class, event -> {
            CashWithdrawnEvent e = (CashWithdrawnEvent) event;
            return List.of(new AccountingEntry(
                AccountType.CLIENT_DEPOSIT,
                AccountType.CASH_DESK,
                e.getAmount(),
                "Снятие наличных",
                e.getEventId().getUuid().toString()
            ));
        });
        
        rules.put(InterestAccruedEvent.class, event -> {
            InterestAccruedEvent e = (InterestAccruedEvent) event;
            return List.of(new AccountingEntry(
                AccountType.EXPENSE_INTEREST,
                AccountType.INTEREST_PAYABLE,
                e.getAmount(),
                "Начисление процентов",
                e.getEventId().getUuid().toString()
            ));
        });
        
        rules.put(LoanPaymentEvent.class, event -> {
            LoanPaymentEvent e = (LoanPaymentEvent) event;
            List<AccountingEntry> entries = new ArrayList<>();
            entries.add(new AccountingEntry(
                AccountType.CASH_DESK,
                AccountType.CLIENT_LOAN,
                e.getPrincipalAmount(),
                "Погашение кредита",
                e.getEventId().getUuid().toString()
            ));
            if (e.getInterestAmount().isPositive()) {
                entries.add(new AccountingEntry(
                    AccountType.CASH_DESK,
                    AccountType.INCOME_INTEREST,
                    e.getInterestAmount(),
                    "Проценты по кредиту",
                    e.getEventId().getUuid().toString()
                ));
            }
            return entries;
        });
        
        rules.put(ProductOpenedEvent.class, event -> {
            ProductOpenedEvent e = (ProductOpenedEvent) event;
            if ("LoanProduct".equals(e.getProductType())) {
                return List.of(new AccountingEntry(
                    AccountType.CLIENT_LOAN,
                    AccountType.CORR_ACCOUNT,
                    e.getAmount(),
                    "Выдача кредита",
                    e.getEventId().getUuid().toString()
                ));
            }
            return List.of();
        });
        
        rules.put(FeeChargedEvent.class, event -> {
            FeeChargedEvent e = (FeeChargedEvent) event;
            return List.of(new AccountingEntry(
                AccountType.CLIENT_DEPOSIT,
                AccountType.INCOME_INTEREST,
                e.getAmount(),
                "Комиссия: " + e.getFeeType(),
                e.getEventId().getUuid().toString()
            ));
        });
    }
    
    public List<AccountingEntry> book(List<Event> events) {
        List<AccountingEntry> allEntries = new ArrayList<>();
        
        for (Event event : events) {
            BookingRule rule = rules.get(event.getClass());
            if (rule != null) {
                List<AccountingEntry> entries = rule.apply(event);
                validateBalance(entries, event);
                allEntries.addAll(entries);
            } else {
                // Логируем, но не падаем — не для всех событий нужны проводки
                // В production здесь должен быть audit log
            }
        }
        
        return allEntries;
    }
    
    /**
     * Правильная проверка двойной записи:
     * сумма дебетовых проводок должна равняться сумме кредитовых.
     */
    private void validateBalance(List<AccountingEntry> entries, Event event) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (AccountingEntry entry : entries) {
            // Проверяем по стороне счета: дебет активного счета = кредит пассивного
            if (entry.getDebitAccount().getSide() == Side.ACTIVE) {
                totalDebit = totalDebit.add(entry.getAmount().getAmount());
            } else {
                totalCredit = totalCredit.add(entry.getAmount().getAmount());
            }
            
            if (entry.getCreditAccount().getSide() == Side.PASSIVE) {
                totalCredit = totalCredit.add(entry.getAmount().getAmount());
            } else {
                totalDebit = totalDebit.add(entry.getAmount().getAmount());
            }
        }
        
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new AccountingBalanceException(
                String.format("Нарушение двойной записи для события %s: Дт=%s, Кт=%s, разница=%s",
                    event.getEventId(), totalDebit, totalCredit, 
                    totalDebit.subtract(totalCredit).abs()));
        }
    }
}