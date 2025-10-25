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

        // 1. Робочі дні, півдня (ранок)
        SkiPass workMorning = registry.issueTimeBased(true, today, today, TimeBasedSkiPass.HalfDayMode.MORNING);

        // 2. Вихідні, повний день (1 день)
        SkiPass weekendDay = registry.issueTimeBased(false, today, today, TimeBasedSkiPass.HalfDayMode.FULL_DAY);

        // 3. Робочі — 2 дні (наприклад, з today по today+1)
        SkiPass twoWorkDays = registry.issueTimeBased(true, today, today.plusDays(1), TimeBasedSkiPass.HalfDayMode.FULL_DAY);

        // 4. Робочі — 5 днів (full days)
        SkiPass fiveWorkDays = registry.issueTimeBased(true, today, today.plusDays(4), TimeBasedSkiPass.HalfDayMode.FULL_DAY);

        // 5. Count-based: 10 підйомів (робочі)
        SkiPass count10Work = registry.issueCountBased(10, true, today, today.plusMonths(1));

        // 6. Count-based: 20 підйомів (вихідні)
        SkiPass count20Weekend = registry.issueCountBased(20, false, today, today.plusMonths(1));

        // 7. Season
        SkiPass season = registry.issueSeason(today.minusDays(5), today.plusMonths(3));
        
        System.out.println("--- Ski Passes ---");
        registry.allPasses().forEach(System.out::println);
        System.out.println();
        
        // Симуляція спроб проходу
        turnstile.attemptPass(workMorning.getId(), LocalDateTime.of(today, LocalTime.of(10,0))); // дозволено (ранок)
        turnstile.attemptPass(workMorning.getId(), LocalDateTime.of(today, LocalTime.of(15,0))); // відмова (після 13:00)

        turnstile.attemptPass(weekendDay.getId(), LocalDateTime.of(today, LocalTime.of(11,0))); // можливо відмовить, якщо сьогодні не вихідний

        // Використаємо count10Work 11 разів — перші 10 дозволено (якщо день робочий), 11-та — відмова
        for (int i = 0; i < 11; i++) {
            LocalTime t = LocalTime.of(11, i % 60);
            turnstile.attemptPass(count10Work.getId(), LocalDateTime.of(today, t));
        }

        // Заблокуємо сезонну картку і спробуємо пройти
        registry.block(season.getId());
        turnstile.attemptPass(season.getId(), LocalDateTime.of(today, LocalTime.of(12,0)));

        // Невідома картка
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
