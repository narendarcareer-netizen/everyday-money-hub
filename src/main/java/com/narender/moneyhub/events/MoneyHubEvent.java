package com.narender.moneyhub.events;

import java.time.Instant;

/** Domain event emitted for account and transfer lifecycle changes. */
public class MoneyHubEvent {

    public enum Type {
        ACCOUNT_CREATED,
        TRANSFER_INITIATED,
        TRANSFER_COMPLETED,
        TRANSFER_FAILED
    }

    private final Type type;
    private final String key;
    private final String payload;
    private final Instant occurredAt = Instant.now();

    public MoneyHubEvent(Type type, String key, String payload) {
        this.type = type;
        this.key = key;
        this.payload = payload;
    }

    public Type getType() { return type; }
    public String getKey() { return key; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }

    @Override
    public String toString() {
        return "{\"type\":\"" + type + "\",\"key\":\"" + key + "\",\"payload\":" + payload + "}";
    }
}
