package com.homeofthewizard.horseracemanager.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RaceInformationDto(@NotNull Long id, @NotNull String name, @NotNull LocalDate date, @NotNull Integer number) {
}
