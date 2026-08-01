package com.bank.accounting;

import com.bank.domain.accounting.AccountType;
import com.bank.domain.accounting.AccountingEntry;
import com.bank.domain.common.Money;
import com.bank.domain.event.Event;
import com.bank.domain.accounting.AccountingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountingService {

    private final PostingProcessor postingProcessor;
    private final BalanceCalculator balanceCalculator;
    private final TrialBalanceVerifier trialBalanceVerifier;
    private final AccountingEngine accountingEngine = new AccountingEngine();

    @Transactional
    public List<AccountingEntry> book(List<Event> events) {
        return accountingEngine.book(events);
    }

    @Transactional
    public void post(List<AccountingEntry> entries) {
        postingProcessor.post(entries);
        balanceCalculator.updateBalances(entries);
    }

    public Money getBalance(AccountType accountType, LocalDate date) {
        return balanceCalculator.getBalance(accountType, date);
    }

    public boolean verifyTrialBalance(LocalDate date) {
        return trialBalanceVerifier.verify(date);
    }
}