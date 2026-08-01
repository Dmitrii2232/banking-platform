package com.bank.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

    private final MeterRegistry registry;
    private final Counter transactionsTotal;
    private final Counter transactionsFailed;
    private final Counter fraudBlocks;
    private final Timer transactionDuration;

    public MetricsCollector(MeterRegistry registry) {
        this.registry = registry;
        this.transactionsTotal = Counter.builder("banking.transactions.total")
            .description("Общее количество транзакций").register(registry);
        this.transactionsFailed = Counter.builder("banking.transactions.failed")
            .description("Неудачные транзакции").register(registry);
        this.fraudBlocks = Counter.builder("banking.fraud.blocks")
            .description("Блокировки фрод-системой").register(registry);
        this.transactionDuration = Timer.builder("banking.transactions.duration")
            .description("Длительность транзакций").register(registry);
    }

    public void recordTransaction() { transactionsTotal.increment(); }
    public void recordTransactionFailed() { transactionsFailed.increment(); }
    public void recordFraudBlock() { fraudBlocks.increment(); }

    public Timer.Sample startTimer() { return Timer.start(registry); }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(transactionDuration);
    }
}