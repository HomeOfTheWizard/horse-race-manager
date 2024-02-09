package com.homeofthewizard.horseracemanager.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RaceDto(@NotNull Long id, @NotNull String name, @NotNull LocalDate date, @NotNull Integer number, List<RaceHorseDto> horses) {
}
