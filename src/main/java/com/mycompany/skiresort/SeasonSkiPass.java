package com.mycompany.skiresort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class SeasonSkiPass extends SkiPass {
    public SeasonSkiPass(LocalDate from, LocalDate to) {
        super(PassCategory.SEASON, from, to);
    }

    @Override
    public Optional<String> validateForPass(LocalDateTime when) {
        return Optional.empty();
    }
}
