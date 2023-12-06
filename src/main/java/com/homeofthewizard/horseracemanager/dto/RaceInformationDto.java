package com.homeofthewizard.horseracemanager.dto;

import java.time.LocalDate;

public record RaceInformationDto(Long id, String name, LocalDate date, Integer number) {
}
