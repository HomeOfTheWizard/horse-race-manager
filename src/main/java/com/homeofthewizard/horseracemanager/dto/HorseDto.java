package com.homeofthewizard.horseracemanager.dto;

import java.util.List;

public record HorseDto(Long id, String name, List<Long> raceIds) {
}
