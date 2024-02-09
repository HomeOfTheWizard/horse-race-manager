package com.homeofthewizard.horseracemanager.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateRaceDto(@NotNull String name,
                            @NotNull
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                            LocalDate date,
                            @NotNull Integer number,
                            @NotNull List<CreateRaceHorseDto> horses) {
}
