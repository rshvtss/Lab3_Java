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

        if (forWorkdays && isWeekend) return Optional.of("Pass working only on Monday-Friday");
        if (!forWorkdays && !isWeekend) return Optional.of("Pass working only on weekends");

        if (halfDayMode != null) {
            LocalTime t = when.toLocalTime();
            switch (halfDayMode) {
                case MORNING:
                    if (t.isBefore(LocalTime.of(9,0)) || t.isAfter(LocalTime.of(13,0)))
                        return Optional.of("Working only at 9 a.m - 1 p.m.");
                    break;
                case AFTERNOON:
                    if (t.isBefore(LocalTime.of(13,0)) || t.isAfter(LocalTime.of(17,0)))
                        return Optional.of("Working only at 1 p.m. - 5 p.m.");
                    break;
                case FULL_DAY:
                    if (t.isBefore(LocalTime.of(9,0)) || t.isAfter(LocalTime.of(17,0)))
                        return Optional.of("Working only at 9 a.m. - 5 p.m.");
                    break;
            }
        }
        return Optional.empty();
    }
}
