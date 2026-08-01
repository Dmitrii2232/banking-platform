package com.bank.aml;

import com.bank.aml.models.SanctionedPerson;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SanctionsListService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<String, SanctionedPerson> localCache = new ConcurrentHashMap<>();
    private boolean initialized = false;

    private static final String SANCTIONS_PREFIX = "sanctioned:";

    public SanctionsListService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            loadInitialData();
            initialized = true;
        } catch (Exception e) {
            log.warn("Не удалось загрузить санкционные списки при старте: {}", e.getMessage());
        }
    }

    public boolean isSanctioned(String clientId) {
        if (!initialized) return false;
        if (localCache.containsKey(clientId)) return true;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(SANCTIONS_PREFIX + clientId));
        } catch (Exception e) {
            log.warn("Ошибка проверки санкций: {}", e.getMessage());
            return false;
        }
    }

    public void addToSanctionsList(String clientId, String reason, String source) {
        SanctionedPerson person = new SanctionedPerson(clientId, reason, source, LocalDateTime.now());
        localCache.put(clientId, person);
        try {
            redisTemplate.opsForValue().set(SANCTIONS_PREFIX + clientId, person, Duration.ofDays(365));
        } catch (Exception e) {
            log.warn("Ошибка добавления в санкционный список: {}", e.getMessage());
        }
    }

    private void loadInitialData() {
        try {
            Set<String> keys = redisTemplate.keys(SANCTIONS_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value instanceof SanctionedPerson person) {
                        localCache.put(person.clientId(), person);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка загрузки санкционных списков: {}", e.getMessage());
        }
    }
}