package com.mycompany.skiresort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class CountBasedSkiPass extends SkiPass {
    private int remainingRides;
    private final boolean forWorkdays;

    // новий конструктор: вказуємо кількість підйомів та чи це для робочих днів
    public CountBasedSkiPass(int rides, boolean forWorkdays, LocalDate from, LocalDate to) {
        super(forWorkdays ? PassCategory.COUNT_WORKDAY : PassCategory.COUNT_WEEKEND, from, to);
        this.remainingRides = rides;
        this.forWorkdays = forWorkdays;
    }

    @Override
    public Optional<String> validateForPass(LocalDateTime when) {
        if (remainingRides <= 0)
            return Optional.of("There is no rides in the pass");

        // Якщо абонемент розрахований тільки на робочі/вихідні — перевіримо день тижня
        boolean isWeekend = when.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                            when.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
        if (forWorkdays && isWeekend) return Optional.of("Pass working only on Monday-Friday");
        if (!forWorkdays && !isWeekend) return Optional.of("Pass working only on weekends");

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
        return super.toString() + String.format(", rides=%d, forWorkdays=%b", remainingRides, forWorkdays);
    }
}
