package com.mycompany.skiresort;

import java.time.*;
import java.util.Optional;

public class TimeBasedSkiPass extends SkiPass {

    public enum HalfDayMode { MORNING, AFTERNOON, FULL_DAY }

    private final HalfDayMode halfDayMode;
    private final boolean forWorkdays;

    public TimeBasedSkiPass(boolean forWorkdays, LocalDate from, LocalDate to, HalfDayMode halfDayMode) {
        super(forWorkdays ? PassCategory.WORKDAY_TIME : PassCategory.WEEKEND_TIME, from, to);
        this.forWorkdays = forWorkdays;
        this.halfDayMode = halfDayMode;
    }

    @Override
    public Optional<String> validateForPass(LocalDateTime when) {
        DayOfWeek dow = when.getDayOfWeek();
        boolean isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);

        if (forWorkdays && isWeekend) return Optional.of("Пропуск діє лише в робочі дні");
        if (!forWorkdays && !isWeekend) return Optional.of("Пропуск діє лише у вихідні");

        LocalTime t = when.toLocalTime();
        switch (halfDayMode) {
            case MORNING:
                // Діапазон [09:00,13:00) — 13:00 належить до AFTERNOON
                if (t.isBefore(LocalTime.of(9,0)) || !t.isBefore(LocalTime.of(13,0)))
                    return Optional.of("Діє тільки з 09:00 до 13:00 (ранкова зміна)");
                break;
            case AFTERNOON:
                // Діапазон [13:00,17:00)
                if (t.isBefore(LocalTime.of(13,0)) || !t.isBefore(LocalTime.of(17,0)))
                    return Optional.of("Діє тільки з 13:00 до 17:00 (післяполуднева зміна)");
                break;
            case FULL_DAY:
                // Діапазон [09:00,17:00)
                if (t.isBefore(LocalTime.of(9,0)) || !t.isBefore(LocalTime.of(17,0)))
                    return Optional.of("Діє тільки з 09:00 до 17:00 (робочий день)");
                break;
        }
        return Optional.empty();
    }
}
