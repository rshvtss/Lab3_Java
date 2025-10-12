package com.mycompany.skiresort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class CountBasedSkiPass extends SkiPass {
    private int remainingRides;

    public CountBasedSkiPass(int rides, LocalDate from, LocalDate to) {
        super(PassCategory.COUNT_BASED, from, to);
        this.remainingRides = rides;
    }

    @Override
    public Optional<String> validateForPass(LocalDateTime when) {
        if (remainingRides <= 0)
            return Optional.of("There is no rides in the pass");
        return Optional.empty();
    }

    @Override
    public synchronized boolean chargeIfNeeded() {
        if (remainingRides <= 0) return false;
        remainingRides--;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", rides=%d", remainingRides);
    }
}
