package com.homeofthewizard.horseracemanager.repository;

import com.homeofthewizard.horseracemanager.entity.RaceHorse;
import com.homeofthewizard.horseracemanager.entity.RaceHorseKey;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceHorseRepository extends JpaRepository<RaceHorse, RaceHorseKey> {
    List<RaceHorse> findByRaceId(Long raceId, Sort sort);
}
