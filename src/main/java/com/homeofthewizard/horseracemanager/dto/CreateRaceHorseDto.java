package com.homeofthewizard.horseracemanager.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRaceHorseDto(Long id, @NotNull String name) {
}
