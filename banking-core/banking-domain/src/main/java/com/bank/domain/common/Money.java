package com.bank.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Безопасный Value Object для денег.
 * Использует Banker's Rounding (HALF_EVEN) для минимизации накопления ошибки.
 * Внутренняя точность — 10 знаков, отображаемая — 2 знака.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Money implements Comparable<Money> {

    private static final int DISPLAY_SCALE = 2;
    private static final int INTERNAL_SCALE = 10;

    public static final Money ZERO_RUB = new Money(BigDecimal.ZERO, "RUB");
    public static final Money ZERO_USD = new Money(BigDecimal.ZERO, "USD");

    private final BigDecimal internalAmount;
    private final String currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currency) {
        Objects.requireNonNull(amount, "Сумма не может быть null");
        Objects.requireNonNull(currency, "Валюта не может быть null");
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Валюта не может быть пустой");
        }
        try {
            Currency.getInstance(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестная валюта: " + currency, e);
        }
        this.internalAmount = amount.setScale(INTERNAL_SCALE, RoundingMode.HALF_EVEN);
        this.currency = currency.toUpperCase();
    }

    public static Money rubles(String amount) {
        return new Money(new BigDecimal(amount), "RUB");
    }

    public static Money rubles(BigDecimal amount) {
        return new Money(amount, "RUB");
    }

    public static Money dollars(String amount) {
        return new Money(new BigDecimal(amount), "USD");
    }

    public static Money dollars(BigDecimal amount) {
        return new Money(amount, "USD");
    }

    public BigDecimal getAmount() {
        return internalAmount.setScale(DISPLAY_SCALE, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getInternalAmount() {
        return internalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(
            this.internalAmount.add(other.internalAmount),
            this.currency
        );
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(
            this.internalAmount.subtract(other.internalAmount),
            this.currency
        );
    }

    public Money multiply(BigDecimal factor) {
        return new Money(
            this.internalAmount.multiply(factor).setScale(INTERNAL_SCALE, RoundingMode.HALF_EVEN),
            this.currency
        );
    }

    public Money divide(BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Деление на ноль");
        }
        return new Money(
            this.internalAmount.divide(divisor, INTERNAL_SCALE, RoundingMode.HALF_EVEN),
            this.currency
        );
    }

    public Money forDisplay() {
        return new Money(
            this.internalAmount.setScale(DISPLAY_SCALE, RoundingMode.HALF_EVEN),
            this.currency
        );
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount) > 0;
    }

    public boolean isGreaterOrEqual(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount) >= 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount) < 0;
    }

    public boolean isZero() {
        return this.internalAmount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.internalAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.internalAmount.compareTo(BigDecimal.ZERO) < 0;
    }

    public Money negate() {
        return new Money(this.internalAmount.negate(), this.currency);
    }

    public Money abs() {
        return new Money(this.internalAmount.abs(), this.currency);
    }

    public Money min(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount) <= 0 ? this : other;
    }

    public Money max(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount) >= 0 ? this : other;
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Валюты не совпадают: %s и %s", this.currency, other.currency));
        }
    }

    @Override
    public int compareTo(Money other) {
        assertSameCurrency(other);
        return this.internalAmount.compareTo(other.internalAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return internalAmount.compareTo(money.internalAmount) == 0
            && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalAmount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", getAmount().toPlainString(), currency);
    }
}