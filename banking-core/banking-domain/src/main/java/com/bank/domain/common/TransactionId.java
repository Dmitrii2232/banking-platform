package com.bank.domain.common;

import java.util.Objects;
import java.util.UUID;

public final class TransactionId implements Comparable<TransactionId> {
    
    private final UUID uuid;
    
    public TransactionId() {
        this.uuid = UUID.randomUUID();
    }
    
    public TransactionId(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "TransactionId не может быть null");
    }
    
    public TransactionId(String uuid) {
        this(UUID.fromString(uuid));
    }
    
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionId that)) return false;
        return uuid.equals(that.uuid);
    }
    
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
    
    @Override
    public String toString() {
        return uuid.toString();
    }
    
    @Override
    public int compareTo(TransactionId other) {
        return this.uuid.compareTo(other.uuid);
    }
}