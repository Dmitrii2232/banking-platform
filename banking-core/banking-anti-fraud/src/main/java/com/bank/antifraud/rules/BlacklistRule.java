package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class BlacklistRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override public String getRuleName() { return "BLACKLIST"; }
    @Override public boolean isEnabled() { return true; }
    @Override public int getPriority() { return 0; }

    @Override
    public FraudCheckResult evaluate(Transaction transaction) {
        if (isBlacklisted("client:" + transaction.getClientId()))
            return FraudCheckResult.fail(getRuleName(), "Клиент в чёрном списке");
        if (transaction.getDeviceFingerprint() != null
            && isBlacklisted("device:" + transaction.getDeviceFingerprint()))
            return FraudCheckResult.fail(getRuleName(), "Устройство в чёрном списке");
        if (transaction.getIpAddress() != null
            && isBlacklisted("ip:" + transaction.getIpAddress()))
            return FraudCheckResult.fail(getRuleName(), "IP-адрес в чёрном списке");
        return FraudCheckResult.pass();
    }

    private boolean isBlacklisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + key));
    }

    public void addToBlacklist(String entityType, String entityId, String reason) {
        String key = "blacklist:" + entityType + ":" + entityId;
        redisTemplate.opsForValue().set(key, reason, Duration.ofDays(90));
    }

    public void removeFromBlacklist(String entityType, String entityId) {
        redisTemplate.delete("blacklist:" + entityType + ":" + entityId);
    }
}