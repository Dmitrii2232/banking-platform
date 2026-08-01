package com.bank.domain.accounting;

import com.bank.domain.common.Money;
import java.util.Objects;
import java.util.UUID;

public class AccountingEntry {
    
    private final UUID id;
    private final AccountType debitAccount;
    private final AccountType creditAccount;
    private final Money amount;
    private final String description;
    private final String eventId;
    
    public AccountingEntry(AccountType debitAccount, AccountType creditAccount,
                            Money amount, String description, String eventId) {
        this.id = UUID.randomUUID();
        this.debitAccount = Objects.requireNonNull(debitAccount, "Дебетуемый счёт обязателен");
        this.creditAccount = Objects.requireNonNull(creditAccount, "Кредитуемый счёт обязателен");
        this.amount = Objects.requireNonNull(amount, "Сумма обязательна");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Сумма проводки должна быть положительной");
        }
        this.description = description != null ? description : "";
        this.eventId = Objects.requireNonNull(eventId, "ID события обязателен");
    }
    
    public UUID getId() { return id; }
    public AccountType getDebitAccount() { return debitAccount; }
    public AccountType getCreditAccount() { return creditAccount; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getEventId() { return eventId; }
    
    @Override
    public String toString() {
        return String.format("Дт %s / Кт %s = %s", debitAccount.getCode(), creditAccount.getCode(), amount);
    }
}