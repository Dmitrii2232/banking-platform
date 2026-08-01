package com.bank.domain.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bank.domain.command.Command;
import com.bank.domain.command.MakePaymentCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.event.LoanPaymentEvent;
import com.bank.domain.event.ProductOpenedEvent;

public class LoanProduct extends BankProduct {

    private BigDecimal annualRate;
    private int totalMonths;
    @SuppressWarnings("unused")
    private int monthsPaid;
    private Money monthlyPayment;
    private Money totalDebt;
    private LocalDate nextPaymentDate;

    public LoanProduct(ProductId id, ClientId clientId, ProductTerms terms) {
        super(id, clientId, terms);
        this.annualRate = terms.getInterestRate();
        this.totalMonths = terms.getTermMonths();
        this.monthsPaid = 0;
        this.totalDebt = Money.ZERO_RUB;
        this.nextPaymentDate = LocalDate.now().plusMonths(1);
        calculateAnnuityPayment();
    }

    private void calculateAnnuityPayment() {
        if (this.balance.isZero()) {
            this.monthlyPayment = Money.ZERO_RUB;
            return;
        }
        BigDecimal monthlyRate = annualRate.divide(
            BigDecimal.valueOf(12), 10, RoundingMode.HALF_EVEN);
        double r = monthlyRate.doubleValue();
        double pow = Math.pow(1 + r, totalMonths);
        double coefficient = (r * pow) / (pow - 1);
        BigDecimal paymentAmount = this.balance.getAmount()
            .multiply(BigDecimal.valueOf(coefficient))
            .setScale(2, RoundingMode.HALF_EVEN);
        this.monthlyPayment = new Money(paymentAmount, this.balance.getCurrency());
        this.totalDebt = this.monthlyPayment.multiply(BigDecimal.valueOf(totalMonths));
    }

    @Override
    public List<Event> apply(Command cmd) {
        List<Event> events = new ArrayList<>();

        if (cmd instanceof MakePaymentCommand payment) {
            assertOperable();
            if (payment.getAmount().isLessThan(monthlyPayment)) {
                throw new IllegalArgumentException(
                    "Минимальный платёж: " + monthlyPayment
                    + ", получено: " + payment.getAmount());
            }
            Money interestPart = calculateInterestForPeriod();
            Money principalPart = payment.getAmount().subtract(interestPart);
            if (principalPart.isGreaterThan(this.balance)) {
                principalPart = this.balance;
            }
            this.balance = this.balance.subtract(principalPart);
            this.monthsPaid++;
            if (this.balance.isZero()) {
                this.status = ProductStatus.CLOSED;
            }
            this.nextPaymentDate = this.nextPaymentDate.plusMonths(1);
            touch();
            events.add(new LoanPaymentEvent(EventId.generate(), this.id, this.clientId,
                payment.getAmount(), principalPart, interestPart, this.balance,
                this.nextPaymentDate, LocalDateTime.now()));
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
    } else if (event instanceof LoanPaymentEvent e) {
        this.balance = e.getRemainingBalance();
        this.monthsPaid++;
        this.nextPaymentDate = e.getNextPaymentDate();
        if (this.balance.isZero()) this.status = ProductStatus.CLOSED;
    }
    this.version++;
}

    private Money calculateInterestForPeriod() {
        BigDecimal monthlyRate = annualRate.divide(
            BigDecimal.valueOf(12), 10, RoundingMode.HALF_EVEN);
        return new Money(
            this.balance.getAmount().multiply(monthlyRate).setScale(2, RoundingMode.HALF_EVEN),
            this.balance.getCurrency());
    }

    public LocalDate getNextPaymentDate() { return nextPaymentDate; }
    public Money getMonthlyPayment() { return monthlyPayment; }
    public Money getTotalDebt() { return totalDebt; }
}