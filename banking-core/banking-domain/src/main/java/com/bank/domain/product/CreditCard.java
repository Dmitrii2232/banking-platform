package com.bank.domain.product;

import com.bank.domain.command.AccrueInterestCommand;
import com.bank.domain.command.CloseProductCommand;
import com.bank.domain.command.Command;
import com.bank.domain.command.MakePaymentCommand;
import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.CashWithdrawnEvent;
import com.bank.domain.event.Event;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.LoanPaymentEvent;
import com.bank.domain.event.ProductOpenedEvent;
import com.bank.domain.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreditCard extends BankProduct {

    private Money creditLimit;
    private Money usedCredit;
    private Money availableCredit;
    private BigDecimal purchaseRate;
    @SuppressWarnings("unused")
    private BigDecimal cashAdvanceRate;
    @SuppressWarnings("unused")
    private int gracePeriodDays;
    private LocalDate statementDate;
    private LocalDate paymentDueDate;
    private Money minPayment;
    private Money totalDebt;

    public CreditCard(ProductId id, ClientId clientId, ProductTerms terms) {
        super(id, clientId, terms);
        this.creditLimit = terms.getCreditLimit() != null
            ? terms.getCreditLimit() : Money.rubles("100000");
        this.usedCredit = Money.ZERO_RUB;
        this.availableCredit = this.creditLimit;
        this.purchaseRate = terms.getInterestRate() != null
            ? terms.getInterestRate() : new BigDecimal("0.25");
        this.cashAdvanceRate = new BigDecimal("0.35");
        this.gracePeriodDays = terms.getGracePeriodDays() != null
            ? terms.getGracePeriodDays() : 55;
        this.statementDate = LocalDate.now().withDayOfMonth(1);
        this.paymentDueDate = this.statementDate.plusDays(25);
        this.minPayment = Money.ZERO_RUB;
        this.totalDebt = Money.ZERO_RUB;
    }

    @Override
    public List<Event> apply(Command cmd) {
        List<Event> events = new ArrayList<>();

        if (cmd instanceof WithdrawCashCommand purchase) {
            assertOperable();
            Money amount = purchase.getAmount();
            if (amount.isGreaterThan(availableCredit)) {
                throw new InsufficientFundsException(
                    "Превышен кредитный лимит: запрошено " + amount
                    + ", доступно " + availableCredit);
            }
            usedCredit = usedCredit.add(amount);
            availableCredit = creditLimit.subtract(usedCredit);
            totalDebt = totalDebt.add(amount);
            touch();
            events.add(new CashWithdrawnEvent(EventId.generate(), this.id, this.clientId,
                amount, purchase.getDestinationId(), LocalDateTime.now()));
        }
        else if (cmd instanceof MakePaymentCommand payment) {
            assertOperable();
            if (payment.getAmount().isLessThan(minPayment)
                && !payment.getAmount().equals(totalDebt)) {
                throw new IllegalArgumentException(
                    "Минимальный платёж: " + minPayment
                    + ", получено: " + payment.getAmount());
            }
            Money remaining = payment.getAmount();
            if (remaining.isGreaterThan(totalDebt)) remaining = totalDebt;
            usedCredit = usedCredit.subtract(remaining);
            availableCredit = creditLimit.subtract(usedCredit);
            totalDebt = totalDebt.subtract(remaining);
            if (usedCredit.isZero()) minPayment = Money.ZERO_RUB;
            touch();
            events.add(new LoanPaymentEvent(EventId.generate(), this.id, this.clientId,
                payment.getAmount(), remaining, Money.ZERO_RUB, totalDebt,
                paymentDueDate, LocalDateTime.now()));
        }
        else if (cmd instanceof AccrueInterestCommand accrue) {
            assertOperable();
            if (totalDebt.isPositive()) {
                Money interest = calculateInterest();
                totalDebt = totalDebt.add(interest);
                usedCredit = usedCredit.add(interest);
                availableCredit = creditLimit.subtract(usedCredit);
                minPayment = totalDebt.multiply(new BigDecimal("0.05"));
                if (minPayment.isLessThan(Money.rubles("100"))) {
                    minPayment = Money.rubles("100");
                }
                touch();
                events.add(new InterestAccruedEvent(EventId.generate(), this.id, this.clientId,
                    interest, accrue.getDate(), false, LocalDateTime.now()));
            }
        }
        else if (cmd instanceof CloseProductCommand) {
            if (totalDebt.isPositive()) {
                throw new com.bank.domain.exception.ProductBlockedException(
                    "Нельзя закрыть карту с задолженностью: " + totalDebt);
            }
            events.addAll(close());
        }
        return events;
    }

    @Override
public void replay(Event event) {
    if (event instanceof ProductOpenedEvent) {
        this.status = ProductStatus.ACTIVE;
    } else if (event instanceof CashWithdrawnEvent e) {
        usedCredit = usedCredit.add(e.getAmount());
        availableCredit = creditLimit.subtract(usedCredit);
        totalDebt = totalDebt.add(e.getAmount());
    } else if (event instanceof LoanPaymentEvent e) {
        usedCredit = usedCredit.subtract(e.getPrincipalAmount());
        availableCredit = creditLimit.subtract(usedCredit);
        totalDebt = totalDebt.subtract(e.getPrincipalAmount());
    } else if (event instanceof InterestAccruedEvent e) {
        totalDebt = totalDebt.add(e.getAmount());
        usedCredit = usedCredit.add(e.getAmount());
    }
    this.version++;
}

    private Money calculateInterest() {
        BigDecimal monthlyRate = purchaseRate.divide(
            BigDecimal.valueOf(12), 10, RoundingMode.HALF_EVEN);
        return new Money(
            totalDebt.getAmount().multiply(monthlyRate).setScale(2, RoundingMode.HALF_EVEN),
            totalDebt.getCurrency());
    }

    public Money getTotalDebt() { return totalDebt; }
    public Money getAvailableCredit() { return availableCredit; }
    public Money getCreditLimit() { return creditLimit; }
}