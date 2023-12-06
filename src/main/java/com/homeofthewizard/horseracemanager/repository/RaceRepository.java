package com.homeofthewizard.horseracemanager.repository;

import com.homeofthewizard.horseracemanager.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long> {
}
