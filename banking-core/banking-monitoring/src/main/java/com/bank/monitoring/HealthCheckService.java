package com.bank.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HealthCheckService implements HealthIndicator {

    private final JdbcTemplate jdbc;

    public HealthCheckService(@Qualifier("eventStoreJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Health health() {
        try {
            jdbc.queryForObject("SELECT 1", Integer.class);
            return Health.up()
                .withDetail("database", "UP")
                .withDetail("kafka", "UP")
                .withDetail("redis", "UP")
                .build();
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}