package com.homeofthewizard.horseracemanager.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateRaceDto(String name, LocalDate date, Integer number, List<RaceHorseDto> horses) {
}
