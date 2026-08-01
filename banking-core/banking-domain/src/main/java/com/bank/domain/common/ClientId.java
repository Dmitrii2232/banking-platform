package com.bank.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;

public final class ClientId implements Comparable<ClientId> {
    private final UUID uuid;
    
    @JsonCreator
    public ClientId(@JsonProperty("uuid") UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "ClientId не может быть null");
    }
    
    public ClientId(String uuid) { this(UUID.fromString(uuid)); }
    public static ClientId generate() { return new ClientId(UUID.randomUUID()); }
    public static ClientId fromString(String uuid) { return new ClientId(UUID.fromString(uuid)); }
    public UUID getUuid() { return uuid; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof ClientId that)) return false; return uuid.equals(that.uuid); }
    @Override public int hashCode() { return uuid.hashCode(); }
    @Override public String toString() { return uuid.toString(); }
    @Override public int compareTo(ClientId other) { return this.uuid.compareTo(other.uuid); }
}