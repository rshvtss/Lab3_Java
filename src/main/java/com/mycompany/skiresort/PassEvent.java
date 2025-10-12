package com.mycompany.skiresort;

import java.time.LocalDateTime;
import java.util.UUID;

public class PassEvent {
    private final UUID passId;
    private final PassEventType type;
    private final PassCategory category;
    private final LocalDateTime timestamp;
    private final String reason;

    public PassEvent(UUID passId, PassEventType type, PassCategory category, LocalDateTime timestamp, String reason) {
        this.passId = passId;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public PassEventType getType() { return type; }
    public PassCategory getCategory() { return category; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getReason() { return reason; }
    public UUID getPassId() { return passId; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s) %s",
                timestamp, passId, type, category, (reason != null ? ("reason: " + reason) : ""));
    }
}
