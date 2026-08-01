package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DeviceFingerprintRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override public String getRuleName() { return "DEVICE_FINGERPRINT"; }
    @Override public boolean isEnabled() { return true; }
    @Override public int getPriority() { return 4; }

    @Override
    public FraudCheckResult evaluate(Transaction transaction) {
        if (transaction.getDeviceFingerprint() == null) return FraudCheckResult.pass();

        String deviceKey = "device:clients:" + transaction.getDeviceFingerprint();
        String clientDeviceKey = "client:devices:" + transaction.getClientId();

        Long clientsOnDevice = redisTemplate.opsForSet().size(deviceKey);
        if (clientsOnDevice != null && clientsOnDevice > 3) {
            return FraudCheckResult.fail(getRuleName(),
                String.format("Устройство используется %d разными клиентами", clientsOnDevice));
        }

        Long devicesForClient = redisTemplate.opsForSet().size(clientDeviceKey);
        if (devicesForClient != null && devicesForClient > 10) {
            return FraudCheckResult.warn(getRuleName(),
                String.format("Клиент использует %d устройств", devicesForClient));
        }

        redisTemplate.opsForSet().add(deviceKey, transaction.getClientId());
        redisTemplate.expire(deviceKey, Duration.ofDays(7));
        redisTemplate.opsForSet().add(clientDeviceKey, transaction.getDeviceFingerprint());
        redisTemplate.expire(clientDeviceKey, Duration.ofDays(7));

        return FraudCheckResult.pass();
    }
}