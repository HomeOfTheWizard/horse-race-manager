package com.homeofthewizard.horseracemanager.dto;

import jakarta.validation.constraints.NotNull;

public record RaceHorseDto(@NotNull Integer no, Long id, @NotNull String name) {
}
