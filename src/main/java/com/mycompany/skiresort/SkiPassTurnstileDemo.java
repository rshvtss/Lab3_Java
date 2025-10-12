package com.mycompany.skiresort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public class SkiPassTurnstileDemo {
    public static void main(String[] args) {
        SkiPassRegistry registry = new SkiPassRegistry();
        Turnstile turnstile = new Turnstile(registry);
        LocalDate today = LocalDate.now();

        SkiPass morning = registry.issueTimeBased(true, today, today, TimeBasedSkiPass.HalfDayMode.MORNING);
        SkiPass count10 = registry.issueCountBased(10, today, today.plusMonths(1));
        SkiPass season = registry.issueSeason(today.minusDays(5), today.plusMonths(3));

        turnstile.attemptPass(morning.getId(), LocalDateTime.of(today, LocalTime.of(10,0))); // дозволено
        turnstile.attemptPass(morning.getId(), LocalDateTime.of(today, LocalTime.of(15,0))); // відмова
        for (int i=0; i<11; i++)
            turnstile.attemptPass(count10.getId(), LocalDateTime.of(today, LocalTime.of(11, i)));
        registry.block(season.getId());
        turnstile.attemptPass(season.getId(), LocalDateTime.of(today, LocalTime.of(12,0)));
        turnstile.attemptPass(UUID.randomUUID(), LocalDateTime.now());

        System.out.println("\n--- Events ---");
        turnstile.getEvents().forEach(System.out::println);

        System.out.println("\n--- Summary stats ---");
        Map<PassEventType, Long> summary = turnstile.getSummaryCounts();
        summary.forEach((k,v) -> System.out.println(k + ": " + v));

        System.out.println("\n--- Stats by types ---");
        turnstile.getCountsByCategory().forEach((cat, data) -> {
            System.out.println(cat);
            data.forEach((k,v) -> System.out.println("  " + k + ": " + v));
        });
    }
}
