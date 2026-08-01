package com.bank.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AlertingService {

    public void sendCriticalAlert(String message) {
        log.error("!!! КРИТИЧЕСКИЙ АЛЕРТ: {} - {}", LocalDateTime.now(), message);
    }

    public void sendHighPriorityAlert(String message) {
        log.warn("!!! ВЫСОКИЙ ПРИОРИТЕТ: {} - {}", LocalDateTime.now(), message);
    }

    public void sendWarning(String message) {
        log.warn("WARNING: {} - {}", LocalDateTime.now(), message);
    }
}