package com.bank.domain.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bank.domain.command.AccrueInterestCommand;
import com.bank.domain.command.CloseProductCommand;
import com.bank.domain.command.Command;
import com.bank.domain.command.DepositCashCommand;
import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.CashDepositedEvent;
import com.bank.domain.event.CashWithdrawnEvent;
import com.bank.domain.event.Event;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.ProductClosedEvent;
import com.bank.domain.event.ProductOpenedEvent;

public class TermDeposit extends BankProduct {

    private LocalDate maturityDate;
    private LocalDate lastInterestDate;

    public TermDeposit(ProductId id, ClientId clientId, ProductTerms terms) {
        super(id, clientId, terms);
        if (terms.getTermMonths() == null || terms.getTermMonths() <= 0) {
            throw new IllegalArgumentException("Срок вклада обязателен и должен быть > 0");
        }
        if (terms.getInterestRate() == null
            || terms.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Процентная ставка обязательна и должна быть > 0");
        }
        this.maturityDate = LocalDate.now().plusMonths(terms.getTermMonths());
        this.lastInterestDate = LocalDate.now();
    }

    @Override
    public List<Event> apply(Command cmd) {
        List<Event> events = new ArrayList<>();

        if (cmd instanceof DepositCashCommand deposit) {
            assertOperable();
            if (maturityDate != null && LocalDate.now().isAfter(maturityDate)) {
                throw new IllegalStateException("Вклад достиг срока погашения");
            }
            if (!terms.isReplenishable()) {
                throw new IllegalStateException("Вклад не является пополняемым");
            }
            if (terms.getMaxBalance() != null
                && this.balance.add(deposit.getAmount()).isGreaterThan(terms.getMaxBalance())) {
                throw new IllegalArgumentException("Превышена максимальная сумма вклада");
            }
            this.balance = this.balance.add(deposit.getAmount());
            touch();
            events.add(new CashDepositedEvent(EventId.generate(), this.id, this.clientId,
                deposit.getAmount(), deposit.getSourceId(), LocalDateTime.now()));
        }
        else if (cmd instanceof AccrueInterestCommand accrue) {
            assertOperable();
            Money interest = calculateInterest(accrue.getDate());
            if (interest.isPositive()) {
                if (terms.isCapitalization()) {
                    this.balance = this.balance.add(interest);
                }
                this.lastInterestDate = accrue.getDate();
                touch();
                events.add(new InterestAccruedEvent(EventId.generate(), this.id, this.clientId,
                    interest, accrue.getDate(), terms.isCapitalization(), LocalDateTime.now()));
            }
        }
        else if (cmd instanceof WithdrawCashCommand withdraw) {
            assertOperable();
            if (!terms.isPartialWithdrawal()) {
                throw new IllegalStateException("Частичное снятие запрещено");
            }
            if (withdraw.getAmount().isGreaterThan(this.balance)) {
                throw new com.bank.domain.exception.InsufficientFundsException(
                    "Недостаточно средств: запрошено " + withdraw.getAmount()
                    + ", доступно " + this.balance);
            }
            Money newBalance = this.balance.subtract(withdraw.getAmount());
            if (newBalance.isLessThan(terms.getMinBalance())) {
                throw new IllegalStateException(
                    "Нельзя уменьшить остаток ниже минимального: " + terms.getMinBalance());
            }
            this.balance = newBalance;
            touch();
            events.add(new CashWithdrawnEvent(EventId.generate(), this.id, this.clientId,
                withdraw.getAmount(), withdraw.getDestinationId(), LocalDateTime.now()));
        }
        else if (cmd instanceof CloseProductCommand) {
            if (LocalDate.now().isBefore(maturityDate)) {
                BigDecimal demandRate = new BigDecimal("0.0001");
                @SuppressWarnings("unused")
                Money recalculatedInterest = calculateInterest(LocalDate.now(), demandRate);
            }
            events.addAll(close());
        }
        else {
            throw new UnsupportedOperationException(
                "Неизвестная команда: " + cmd.getClass().getSimpleName());
        }
        return events;
    }

    @Override
public void replay(Event event) {
    if (event instanceof ProductOpenedEvent) {
        this.status = ProductStatus.ACTIVE;
    } else if (event instanceof CashDepositedEvent e) {
        this.balance = this.balance.add(e.getAmount());
    } else if (event instanceof InterestAccruedEvent e) {
        if (e.isCapitalization()) this.balance = this.balance.add(e.getAmount());
        this.lastInterestDate = e.getAccrualDate();
    } else if (event instanceof CashWithdrawnEvent e) {
        this.balance = this.balance.subtract(e.getAmount());
    } else if (event instanceof ProductClosedEvent) {
        this.status = ProductStatus.CLOSED;
    }
    this.version++;
}

    private Money calculateInterest(LocalDate date) {
        return calculateInterest(date, terms.getInterestRate());
    }

    private Money calculateInterest(LocalDate date, BigDecimal rate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(lastInterestDate, date);
        if (days <= 0) return Money.ZERO_RUB;
        BigDecimal interestAmount = this.balance.getInternalAmount()
            .multiply(rate)
            .multiply(BigDecimal.valueOf(days))
            .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_EVEN);
        return new Money(interestAmount, this.balance.getCurrency());
    }

    public LocalDate getMaturityDate() { return maturityDate; }
    public boolean isMatured() {
        return LocalDate.now().isAfter(maturityDate) || LocalDate.now().equals(maturityDate);
    }
}