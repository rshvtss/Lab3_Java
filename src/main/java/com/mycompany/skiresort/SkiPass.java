package com.mycompany.skiresort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public abstract class SkiPass {
    private final UUID id;
    private boolean blocked;
    private final PassCategory category;
    private final LocalDate validFrom;
    private final LocalDate validTo;

    protected SkiPass(PassCategory category, LocalDate validFrom, LocalDate validTo) {
        this.id = UUID.randomUUID();
        this.blocked = false;
        this.category = category;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public UUID getId() { return id; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public PassCategory getCategory() { return category; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }

    public boolean isExpired(LocalDateTime when) {
        LocalDate d = when.toLocalDate();
        return d.isBefore(validFrom) || d.isAfter(validTo);
    }

    public abstract Optional<String> validateForPass(LocalDateTime when);
    public boolean chargeIfNeeded() { return true; }

    @Override
    public String toString() {
        return String.format("%s[id=%s,cat=%s,from=%s,to=%s,blocked=%b]",
                this.getClass().getSimpleName(), id, category, validFrom, validTo, blocked);
    }
}
