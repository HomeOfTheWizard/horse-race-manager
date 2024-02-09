package com.homeofthewizard.horseracemanager.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HorseDto(Long id, @NotNull String name, List<Long> raceIds) {
}
