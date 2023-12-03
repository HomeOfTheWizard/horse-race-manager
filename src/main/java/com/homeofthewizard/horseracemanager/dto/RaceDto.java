package com.homeofthewizard.horseracemanager.dto;

import java.time.LocalDate;
import java.util.List;

record RaceDto(Long id, String name, LocalDate date, List<HorseDto> horses) {
}
