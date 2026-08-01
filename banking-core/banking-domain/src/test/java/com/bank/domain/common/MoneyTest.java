package com.bank.domain.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

class MoneyTest {

    @Test
    void shouldAddSameCurrency() {
        Money m1 = Money.rubles("100.00");
        Money m2 = Money.rubles("50.00");
        assertEquals(Money.rubles("150.00"), m1.add(m2));
    }

    @Test
    void shouldThrowOnDifferentCurrencies() {
        Money rub = Money.rubles("100.00");
        Money usd = Money.dollars("50.00");
        assertThrows(IllegalArgumentException.class, () -> rub.add(usd));
    }

    @Test
    void shouldSubtractCorrectly() {
        Money m1 = Money.rubles("100.00");
        Money m2 = Money.rubles("30.00");
        assertEquals(Money.rubles("70.00"), m1.subtract(m2));
    }

    @Test
    void shouldDetectZero() {
        assertTrue(Money.ZERO_RUB.isZero());
        assertFalse(Money.rubles("1.00").isZero());
    }

    @Test
    void shouldDetectNegative() {
        assertTrue(Money.rubles("-1.00").isNegative());
        assertFalse(Money.rubles("1.00").isNegative());
    }

    @Test
    void shouldThrowOnDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> Money.rubles("100").divide(BigDecimal.ZERO));
    }

    @Test
    void shouldMaintainInternalPrecision() {
        Money m = Money.rubles("100.1234567890");
        assertEquals(new BigDecimal("100.12"), m.getAmount()); // display scale
        assertEquals(new BigDecimal("100.1234567890"), m.getInternalAmount()); // internal
    }
}
