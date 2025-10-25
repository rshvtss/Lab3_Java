package com.mycompany.skiresort;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class SkiPassRegistry {
    private final Map<UUID, SkiPass> store = new ConcurrentHashMap<>();

    public SkiPass issueTimeBased(boolean forWorkdays, LocalDate from, LocalDate to, TimeBasedSkiPass.HalfDayMode mode) {
        TimeBasedSkiPass pass = new TimeBasedSkiPass(forWorkdays, from, to, mode);
        store.put(pass.getId(), pass);
        return pass;
    }

    // нова сигнатура: додаємо forWorkdays, щоб розрізняти робочі/вихідні count-based
    public SkiPass issueCountBased(int rides, boolean forWorkdays, LocalDate from, LocalDate to) {
        CountBasedSkiPass pass = new CountBasedSkiPass(rides, forWorkdays, from, to);
        store.put(pass.getId(), pass);
        return pass;
    }

    public SkiPass issueSeason(LocalDate from, LocalDate to) {
        SeasonSkiPass pass = new SeasonSkiPass(from, to);
        store.put(pass.getId(), pass);
        return pass;
    }

    public Optional<SkiPass> get(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean block(UUID id) {
        SkiPass p = store.get(id);
        if (p == null) return false;
        p.setBlocked(true);
        return true;
    }

    public Collection<SkiPass> allPasses() {
        return store.values();
    }
}
