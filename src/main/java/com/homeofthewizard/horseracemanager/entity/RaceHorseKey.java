package com.homeofthewizard.horseracemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RaceHorseKey implements Serializable {

    @Column(name="race_id")
    private Long raceId;

    @Column(name="horse_id")
    private Long horseId;

}