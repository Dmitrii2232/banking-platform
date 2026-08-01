package com.bank.aml;

import com.bank.antifraud.models.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitMonitor {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DAILY_LIMIT_PREFIX = "aml:daily:";
    private static final String MONTHLY_LIMIT_PREFIX = "aml:monthly:";
    private static final String RECENT_TX_PREFIX = "aml:recent:";

    public boolean checkDailyLimit(String clientId, BigDecimal amount) {
        String key = DAILY_LIMIT_PREFIX + clientId + ":" + LocalDate.now();
        BigDecimal currentTotal = getCurrentTotal(key);
        BigDecimal dailyLimit = new BigDecimal("600000");
        return currentTotal.add(amount).compareTo(dailyLimit) <= 0;
    }

    public boolean checkMonthlyLimit(String clientId, BigDecimal amount) {
        String key = MONTHLY_LIMIT_PREFIX + clientId + ":" + LocalDate.now().getYear() + ":" + LocalDate.now().getMonthValue();
        BigDecimal currentTotal = getCurrentTotal(key);
        BigDecimal monthlyLimit = new BigDecimal("5000000");
        return currentTotal.add(amount).compareTo(monthlyLimit) <= 0;
    }

    public void recordTransaction(Transaction transaction) {
        String dailyKey = DAILY_LIMIT_PREFIX + transaction.getClientId() + ":" + LocalDate.now();
        redisTemplate.opsForValue().increment(dailyKey, transaction.getAmount().doubleValue());
        redisTemplate.expire(dailyKey, Duration.ofHours(24));

        String monthlyKey = MONTHLY_LIMIT_PREFIX + transaction.getClientId() + ":" + LocalDate.now().getYear() + ":" + LocalDate.now().getMonthValue();
        redisTemplate.opsForValue().increment(monthlyKey, transaction.getAmount().doubleValue());
        redisTemplate.expire(monthlyKey, Duration.ofDays(31));

        String recentKey = RECENT_TX_PREFIX + transaction.getClientId();
        redisTemplate.opsForList().leftPush(recentKey, transaction);
        redisTemplate.opsForList().trim(recentKey, 0, 49);
    }

    public List<Transaction> getRecentTransactions(String clientId, int count) {
        String key = RECENT_TX_PREFIX + clientId;
        List<Object> objects = redisTemplate.opsForList().range(key, 0, count - 1);
        List<Transaction> transactions = new ArrayList<>();
        if (objects != null) {
            for (Object obj : objects) {
                if (obj instanceof Transaction tx) transactions.add(tx);
            }
        }
        return transactions;
    }

    private BigDecimal getCurrentTotal(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        return BigDecimal.ZERO;
    }
}