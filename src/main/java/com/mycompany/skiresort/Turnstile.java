package com.mycompany.skiresort;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Turnstile {
    private final SkiPassRegistry registry;
    private final List<PassEvent> events = Collections.synchronizedList(new ArrayList<>());

    public Turnstile(SkiPassRegistry registry) {
        this.registry = registry;
    }

    public boolean attemptPass(UUID passId, LocalDateTime when) {
        Optional<SkiPass> opt = registry.get(passId);
        if (!opt.isPresent()) {
            recordEvent(passId, PassEventType.DENIED, null, when, "Unable to read a pass");
            return false;
        }

        SkiPass pass = opt.get();
        if (pass.isBlocked()) {
            recordEvent(pass.getId(), PassEventType.DENIED, pass.getCategory(), when, "Pass is locked");
            return false;
        }

        if (pass.isExpired(when)) {
            recordEvent(pass.getId(), PassEventType.DENIED, pass.getCategory(), when, "Pass is outdated");
            return false;
        }

        Optional<String> validation = pass.validateForPass(when);
        if (validation.isPresent()) {
            recordEvent(pass.getId(), PassEventType.DENIED, pass.getCategory(), when, validation.get());
            return false;
        }

        if (!pass.chargeIfNeeded()) {
            recordEvent(pass.getId(), PassEventType.DENIED, pass.getCategory(), when, "Unable to use a ride");
            return false;
        }

        recordEvent(pass.getId(), PassEventType.ALLOWED, pass.getCategory(), when, null);
        return true;
    }

    private void recordEvent(UUID passId, PassEventType type, PassCategory category, LocalDateTime when, String reason) {
        events.add(new PassEvent(passId, type, category, when, reason));
    }

    public List<PassEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public Map<PassEventType, Long> getSummaryCounts() {
        return events.stream().collect(Collectors.groupingBy(PassEvent::getType, Collectors.counting()));
    }

    public Map<PassCategory, Map<PassEventType, Long>> getCountsByCategory() {
        return events.stream()
                .filter(e -> e.getCategory() != null) // ігноруємо події без категорії
                .collect(Collectors.groupingBy(
                        PassEvent::getCategory,
                        Collectors.groupingBy(PassEvent::getType, Collectors.counting())
                ));
    }
}
