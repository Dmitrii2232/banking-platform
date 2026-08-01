package com.bank.monitoring.dashboard;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusMetrics {
    public PrometheusMetrics(PrometheusMeterRegistry registry) {
    }
}