package com.bank.kafka.topics;

public class KafkaTopics {
    public static final String TRANSACTION_EVENTS_TOPIC = "banking.transaction.events";
    public static final String ACCOUNTING_ENTRIES_TOPIC = "banking.accounting.entries";
    public static final String FRAUD_ALERTS_TOPIC = "banking.fraud.alerts";
    public static final String AML_ALERTS_TOPIC = "banking.aml.alerts";
    public static final String PRODUCT_EVENTS_TOPIC = "banking.product.events";
    public static final String PROJECTION_UPDATES_TOPIC = "banking.projection.updates";
    public static final String DEAD_LETTER_TOPIC = "banking.dlq";

    private KafkaTopics() {}
}