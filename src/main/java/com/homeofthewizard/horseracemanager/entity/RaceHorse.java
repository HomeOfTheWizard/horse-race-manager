package com.homeofthewizard.horseracemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaceHorse {

    @EmbeddedId
    RaceHorseKey id;

    @ManyToOne
    @MapsId("horseId")
    @JoinColumn(name = "horse_id")
    Horse horse;

    @ManyToOne
    @MapsId("raceId")
    @JoinColumn(name = "race_id")
    @JsonIgnore
    Race race;

    private Integer noHorse;
}
