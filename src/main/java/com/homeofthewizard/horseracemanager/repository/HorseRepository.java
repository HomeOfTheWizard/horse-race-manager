package com.homeofthewizard.horseracemanager.repository;

import com.homeofthewizard.horseracemanager.entity.Horse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorseRepository extends JpaRepository<Horse, Long> {}