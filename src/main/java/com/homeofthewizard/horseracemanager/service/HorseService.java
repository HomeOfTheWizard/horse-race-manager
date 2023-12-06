package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.dto.HorseDto;
import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.entity.RaceHorse;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class HorseService {

    private static final String HORSE_TOPIC = "horse-topic";

    private HorseRepository repository;
    private KafkaProducer kafkaProducer;

    public List<HorseDto> findAll(){
        return repository.findAll().stream().map(this::horseToDTO).toList();
    }

    private HorseDto horseToDTO(Horse horse) {
        return new HorseDto(
                horse.getId(),
                horse.getName(),
                horse.getRaces().stream().map(RaceHorse::getRace).map(Race::getId).toList());
    }

    @Transactional
    public HorseDto save(HorseDto horse) {
        var horseDbo = getHorse(horse);
        repository.save(horseDbo);
        kafkaProducer.sendToKafkaTopic(horse, HORSE_TOPIC);
        return horseToDTO(horseDbo);
    }

    private Horse getHorse(HorseDto horseDto) {
        if(Objects.isNull(horseDto.id()))
            return new Horse(null, horseDto.name(), Set.of());
        else
            return repository.findById(horseDto.id()).orElse(new Horse(null, horseDto.name(), Set.of()));
    }
}
