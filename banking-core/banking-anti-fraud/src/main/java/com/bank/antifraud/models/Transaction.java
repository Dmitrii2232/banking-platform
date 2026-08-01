package com.bank.antifraud.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final String transactionId;
    private final String clientId;
    private final BigDecimal amount;
    private final String currency;
    private final String type;
    private final String sourceProductId;
    private final String destinationProductId;
    private final String description;
    private final String recipientId;
    private final String deviceFingerprint;
    private final String ipAddress;
    private final GeoLocation geoLocation;
    private final LocalDateTime timestamp;
    private final String status;

    private Transaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.clientId = builder.clientId;
        this.amount = builder.amount;
        this.currency = builder.currency != null ? builder.currency : "RUB";
        this.type = builder.type;
        this.sourceProductId = builder.sourceProductId;
        this.destinationProductId = builder.destinationProductId;
        this.description = builder.description;
        this.recipientId = builder.recipientId;
        this.deviceFingerprint = builder.deviceFingerprint;
        this.ipAddress = builder.ipAddress;
        this.geoLocation = builder.geoLocation;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.status = builder.status != null ? builder.status : "PENDING";
    }

    public static Builder builder() { return new Builder(); }

    public String getTransactionId() { return transactionId; }
    public String getClientId() { return clientId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getSourceProductId() { return sourceProductId; }
    public String getDestinationProductId() { return destinationProductId; }
    public String getDescription() { return description; }
    public String getRecipientId() { return recipientId; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public String getIpAddress() { return ipAddress; }
    public GeoLocation getGeoLocation() { return geoLocation; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    public static class Builder {
        private String transactionId;
        private String clientId;
        private BigDecimal amount;
        private String currency;
        private String type;
        private String sourceProductId;
        private String destinationProductId;
        private String description;
        private String recipientId;
        private String deviceFingerprint;
        private String ipAddress;
        private GeoLocation geoLocation;
        private LocalDateTime timestamp;
        private String status;

        public Builder transactionId(String v) { transactionId = v; return this; }
        public Builder clientId(String v) { clientId = v; return this; }
        public Builder amount(BigDecimal v) { amount = v; return this; }
        public Builder currency(String v) { currency = v; return this; }
        public Builder type(String v) { type = v; return this; }
        public Builder sourceProductId(String v) { sourceProductId = v; return this; }
        public Builder destinationProductId(String v) { destinationProductId = v; return this; }
        public Builder description(String v) { description = v; return this; }
        public Builder recipientId(String v) { recipientId = v; return this; }
        public Builder deviceFingerprint(String v) { deviceFingerprint = v; return this; }
        public Builder ipAddress(String v) { ipAddress = v; return this; }
        public Builder geoLocation(GeoLocation v) { geoLocation = v; return this; }
        public Builder timestamp(LocalDateTime v) { timestamp = v; return this; }
        public Builder status(String v) { status = v; return this; }
        public Transaction build() { return new Transaction(this); }
    }
}