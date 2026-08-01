package com.bank.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;

public final class EventId implements Comparable<EventId> {
    
    private final UUID uuid;
    private final long version;
    
    @JsonCreator
    public EventId(@JsonProperty("uuid") UUID uuid, @JsonProperty("version") long version) {
        this.uuid = Objects.requireNonNull(uuid, "UUID события не может быть null");
        if (version < 1) {
            throw new IllegalArgumentException("Версия события должна быть >= 1");
        }
        this.version = version;
    }
    
    public static EventId generate() {
        return new EventId(UUID.randomUUID(), 1L);
    }
    
    public EventId nextVersion() {
        return new EventId(this.uuid, this.version + 1);
    }
    
    public UUID getUuid() { return uuid; }
    public long getVersion() { return version; }
    
    @Override public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof EventId that)) return false; return version == that.version && uuid.equals(that.uuid); }
    @Override public int hashCode() { return Objects.hash(uuid, version); }
    @Override public String toString() { return uuid + "@v" + version; }
    @Override public int compareTo(EventId other) { int c = this.uuid.compareTo(other.uuid); return c != 0 ? c : Long.compare(this.version, other.version); }
}