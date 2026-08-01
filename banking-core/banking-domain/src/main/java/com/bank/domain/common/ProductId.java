package com.bank.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;

public final class ProductId implements Comparable<ProductId> {
    private final UUID uuid;
    
    @JsonCreator
    public ProductId(@JsonProperty("uuid") UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "ProductId не может быть null");
    }
    
    public ProductId(String uuid) { this(UUID.fromString(uuid)); }
    public static ProductId generate() { return new ProductId(UUID.randomUUID()); }
    public UUID getUuid() { return uuid; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof ProductId that)) return false; return uuid.equals(that.uuid); }
    @Override public int hashCode() { return uuid.hashCode(); }
    @Override public String toString() { return uuid.toString(); }
    @Override public int compareTo(ProductId other) { return this.uuid.compareTo(other.uuid); }
}