package com.bank.domain.product;

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
import com.bank.domain.event.FeeChargedEvent;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.MasterAccountChangedEvent;
import com.bank.domain.event.ProductClosedEvent;
import com.bank.domain.event.ProductOpenedEvent;
import com.bank.domain.exception.InsufficientFundsException;
import com.bank.domain.exception.ProductBlockedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CurrentAccount extends BankProduct {

    private Money overdraftLimit;
    private Money usedOverdraft;
    private BigDecimal overdraftRate;
    private int freeTransactionsPerMonth;
    private int transactionsThisMonth;
    private Money transactionFee;

    public CurrentAccount(ProductId id, ClientId clientId, ProductTerms terms) {
        super(id, clientId, terms);
        this.overdraftLimit = terms.getCreditLimit() != null
            ? terms.getCreditLimit() : Money.ZERO_RUB;
        this.usedOverdraft = Money.ZERO_RUB;
        this.overdraftRate = terms.getInterestRate();
        this.freeTransactionsPerMonth = 50;
        this.transactionsThisMonth = 0;
        this.transactionFee = terms.getMonthlyFee();
    }

    @Override
    public List<Event> apply(Command cmd) {
        // CRITICAL: проверка владения
        assertOwnedBy(cmd.getClientId());
        
        List<Event> events = new ArrayList<>();

        if (cmd instanceof DepositCashCommand deposit) {
            assertOperable();
            if (usedOverdraft.isPositive()) {
                Money toOverdraft = deposit.getAmount();
                if (toOverdraft.isGreaterThan(usedOverdraft)) toOverdraft = usedOverdraft;
                usedOverdraft = usedOverdraft.subtract(toOverdraft);
                Money remaining = deposit.getAmount().subtract(toOverdraft);
                this.balance = this.balance.add(remaining);
                events.add(new FeeChargedEvent(EventId.generate(), this.id, this.clientId,
                    toOverdraft, "OVERDRAFT_REPAYMENT", LocalDateTime.now()));
            } else {
                this.balance = this.balance.add(deposit.getAmount());
            }
            touch();
            events.add(new CashDepositedEvent(EventId.generate(), this.id, this.clientId,
                deposit.getAmount(), deposit.getSourceId(), LocalDateTime.now()));
        }
        else if (cmd instanceof WithdrawCashCommand withdraw) {
            assertOperable();
            checkTransactionLimit();
            Money available = this.balance.add(overdraftLimit.subtract(usedOverdraft));
            if (withdraw.getAmount().isGreaterThan(available)) {
                throw new InsufficientFundsException("Недостаточно средств: запрошено "
                    + withdraw.getAmount() + ", доступно " + available);
            }
            Money remaining = withdraw.getAmount();
            if (remaining.isGreaterThan(this.balance)) {
                Money overdraftUsed = remaining.subtract(this.balance);
                this.balance = Money.ZERO_RUB;
                this.usedOverdraft = this.usedOverdraft.add(overdraftUsed);
            } else {
                this.balance = this.balance.subtract(remaining);
            }
            this.transactionsThisMonth++;
            touch();
            events.add(new CashWithdrawnEvent(EventId.generate(), this.id, this.clientId,
                withdraw.getAmount(), withdraw.getDestinationId(), LocalDateTime.now()));
            if (transactionsThisMonth > freeTransactionsPerMonth && transactionFee.isPositive()) {
                this.balance = this.balance.subtract(transactionFee);
                events.add(new FeeChargedEvent(EventId.generate(), this.id, this.clientId,
                    transactionFee, "TRANSACTION_EXCESS_FEE", LocalDateTime.now()));
            }
        }
        else if (cmd instanceof AccrueInterestCommand accrue) {
            assertOperable();
            if (usedOverdraft.isPositive()) {
                Money overdraftInterest = calculateOverdraftInterest();
                this.usedOverdraft = this.usedOverdraft.add(overdraftInterest);
                events.add(new InterestAccruedEvent(EventId.generate(), this.id, this.clientId,
                    overdraftInterest, accrue.getDate(), false, LocalDateTime.now()));
            }
            if (this.balance.isPositive()
                && terms.getInterestRate().compareTo(BigDecimal.ZERO) > 0) {
                Money creditInterest = calculateCreditInterest();
                this.balance = this.balance.add(creditInterest);
                events.add(new InterestAccruedEvent(EventId.generate(), this.id, this.clientId,
                    creditInterest, accrue.getDate(), true, LocalDateTime.now()));
            }
            touch();
        }
        else if (cmd instanceof CloseProductCommand) {
            if (usedOverdraft.isPositive()) {
                throw new ProductBlockedException(
                    "Нельзя закрыть счёт с непогашенным овердрафтом: " + usedOverdraft);
            }
            events.addAll(close());
        }
        return events;
    }

    @Override
    public void replay(Event event) {
        if (event instanceof ProductOpenedEvent) {
            this.status = ProductStatus.ACTIVE;
        } else if (event instanceof CashDepositedEvent e) {
            this.balance = this.balance.add(e.getAmount());
        } else if (event instanceof CashWithdrawnEvent e) {
            this.balance = this.balance.subtract(e.getAmount());
        } else if (event instanceof InterestAccruedEvent e && e.isCapitalization()) {
            this.balance = this.balance.add(e.getAmount());
        } else if (event instanceof FeeChargedEvent e) {
            this.balance = this.balance.subtract(e.getAmount());
        } else if (event instanceof ProductClosedEvent) {
            this.status = ProductStatus.CLOSED;
        } else if (event instanceof MasterAccountChangedEvent e) {
            this.isMaster = this.id.toString().equals(e.getNewMasterProductId());
        }
        this.version++;
    }

    private Money calculateOverdraftInterest() {
        BigDecimal dailyRate = overdraftRate.divide(
            BigDecimal.valueOf(365), 10, RoundingMode.HALF_EVEN);
        BigDecimal interest = usedOverdraft.getAmount()
            .multiply(dailyRate).multiply(BigDecimal.valueOf(30));
        return new Money(interest.setScale(2, RoundingMode.HALF_EVEN), usedOverdraft.getCurrency());
    }

    private Money calculateCreditInterest() {
        BigDecimal dailyRate = terms.getInterestRate().divide(
            BigDecimal.valueOf(365), 10, RoundingMode.HALF_EVEN);
        BigDecimal interest = this.balance.getAmount()
            .multiply(dailyRate).multiply(BigDecimal.valueOf(30));
        return new Money(interest.setScale(2, RoundingMode.HALF_EVEN), this.balance.getCurrency());
    }

    private void checkTransactionLimit() {
        if (transactionsThisMonth >= freeTransactionsPerMonth + 100) {
            throw new ProductBlockedException("Превышен лимит транзакций в месяц");
        }
    }

    public Money getAvailableBalance() {
        return this.balance.add(overdraftLimit.subtract(usedOverdraft));
    }
}