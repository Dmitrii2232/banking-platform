package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Component
public class VelocityRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${fraud.rules.velocity.max-transactions-5min:10}")
    private int maxTransactions5min;

    @Value("${fraud.rules.velocity.max-amount-15min-rub:500000}")
    private BigDecimal maxAmount15min;

    public VelocityRule(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override public String getRuleName() { return "VELOCITY"; }
    @Override public boolean isEnabled() { return true; }
    @Override public int getPriority() { return 1; }

    @Override
    public FraudCheckResult evaluate(Transaction transaction) {
        String clientId = transaction.getClientId();
        String countKey = "velocity:count:" + clientId;
        String amountKey = "velocity:amount:" + clientId;

        Object countObj = redisTemplate.opsForValue().get(countKey);
        int count = countObj instanceof Integer i ? i : 0;

        if (count > maxTransactions5min) {
            return FraudCheckResult.fail(getRuleName(),
                String.format("Более %d транзакций за 5 минут (текущее: %d)", maxTransactions5min, count));
        }

        Object amountObj = redisTemplate.opsForValue().get(amountKey);
        BigDecimal totalAmount = amountObj instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.ZERO;

        if (totalAmount.compareTo(maxAmount15min) > 0) {
            return FraudCheckResult.fail(getRuleName(), "Сумма транзакций за 15 мин > " + maxAmount15min);
        }

        redisTemplate.opsForValue().increment(countKey);
        redisTemplate.expire(countKey, Duration.ofMinutes(5));
        redisTemplate.opsForValue().increment(amountKey, transaction.getAmount().doubleValue());
        redisTemplate.expire(amountKey, Duration.ofMinutes(15));

        return FraudCheckResult.pass();
    }
}