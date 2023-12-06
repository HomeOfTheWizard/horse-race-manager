package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.dto.CreateRaceDto;
import com.homeofthewizard.horseracemanager.dto.RaceDto;
import com.homeofthewizard.horseracemanager.dto.RaceHorseDto;
import com.homeofthewizard.horseracemanager.dto.RaceInformationDto;
import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.entity.RaceHorse;
import com.homeofthewizard.horseracemanager.entity.RaceHorseKey;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceHorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class RaceService {

    private static final String RACE_TOPIC = "race-topic";
    private static final String RACE_SIGNUP_TOPIC = "race-signup-topic";
    private static final String RACE_DROPOUT_TOPIC = "race-dropout-topic";

    private RaceRepository repository;
    private HorseRepository horseRepository;
    private RaceHorseRepository raceHorseRepository;
    private KafkaProducer kafkaProducer;

    public List<RaceDto> findAll(){
        return repository.findAll().stream().map(this::raceToDTO).toList();
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public RaceDto save(CreateRaceDto race) {
        var raceValidated = validateRace(race);
        var racePersisted = repository.save(raceValidated);
        kafkaProducer.sendToKafkaTopic(racePersisted, RACE_TOPIC);
        return raceToDTO(racePersisted);
    }

    public RaceDto update(RaceInformationDto race) {
        //TODO
        return null;
    }

    @Transactional(rollbackFor = {NoSuchElementException.class, IllegalArgumentException.class})
    public RaceDto signUp(Long horseId, Long raceId){
        var race = repository.findById(raceId).orElseThrow(()->new NoSuchElementException("no race with specified Id has been found"));
        var horse = horseRepository.findById(horseId).orElseThrow(()->new NoSuchElementException("no horse with specified Id has been found"));

        var horses = new ArrayList<>(race.getHorses());
        if (isHorseIdPartOfTheHorseList(horseId, horses))
            throw new IllegalArgumentException("Horse is already signed up to the race");
        else {
            race = addHorseToRace(horseId, raceId, race, horse, horses);
            kafkaProducer.sendToKafkaTopic(race, RACE_SIGNUP_TOPIC);
            return raceToDTO(race);
        }
    }

    @Transactional(rollbackFor = {NoSuchElementException.class, IllegalArgumentException.class})
    public RaceDto dropOut(Long horseId, Long raceId) {
        var race = repository.findById(raceId).orElseThrow(()->new NoSuchElementException("no race with specified Id has been found"));
        var horse = horseRepository.findById(horseId).orElseThrow(()->new NoSuchElementException("no horse with specified Id has been found"));

        var horses = new ArrayList<>(race.getHorses());
        if (!isHorseIdPartOfTheHorseList(horseId, horses))
            throw new IllegalArgumentException("Horse has not yet signed up to the race");
        else {
            race = removeHorseFromRace(horseId, raceId, race, horse, horses);
            kafkaProducer.sendToKafkaTopic(race, RACE_DROPOUT_TOPIC);
            return raceToDTO(race);
        }
    }

    private Race addHorseToRace(Long horseId, Long raceId, Race race, Horse horse, ArrayList<RaceHorse> horses) {
        var raceHorse = new RaceHorse(new RaceHorseKey(raceId, horseId), horse, race, horses.size() + 1);
        raceHorseRepository.save(raceHorse);

        horses.add(raceHorse);
        race.setHorses(horses);
        return repository.save(race);
    }

    private Race removeHorseFromRace(Long horseId, Long raceId, Race race, Horse horse, ArrayList<RaceHorse> horses) {
        var raceHorse = horses.stream().filter(rc -> rc.getHorse().getId().equals(horseId)).findAny().get();
        raceHorseRepository.delete(raceHorse);

        horses.remove(raceHorse);
        race.setHorses(horses);
        return repository.save(race);
    }

    private static boolean isHorseIdPartOfTheHorseList(Long id, ArrayList<RaceHorse> horses) {
        return horses.stream().map(RaceHorse::getHorse).map(Horse::getId).anyMatch(id::equals);
    }

    private Race validateRace(CreateRaceDto race) {
        var raceHorseDto = race.horses();
        if(raceHorseDto.size() < 3) throw new IllegalArgumentException("A race cannot include less than 3 horses");

        var raceDbo = new Race(null, race.name(), race.date(), race.number(), List.of());

        var raceHorseDbos = new ArrayList<RaceHorse>();
        for (int i = 0; i< raceHorseDto.size(); i++) {
            var horseDbo = findHorse(raceHorseDto.get(i));
            raceHorseDbos.add(new RaceHorse(new RaceHorseKey(null,null), horseDbo, raceDbo, i+1));
        }

        raceDbo.setHorses(raceHorseDbos);
        return raceDbo;
    }

    private Horse findHorse(RaceHorseDto horseDto) {
        if(Objects.isNull(horseDto.id()))
            return new Horse(null, horseDto.name(), Set.of());
        else
            return horseRepository.findById(horseDto.id()).orElse(new Horse(null, horseDto.name(), Set.of()));
    }

    private Race findRace(RaceDto race) {
        if(Objects.isNull(race.id()))
            return new Race(null, race.name(), race.date(), race.number(), List.of());
        else
            return repository.findById(race.id())
                    .orElse(new Race(null, race.name(), race.date(), race.number(), List.of()));
    }

    private RaceDto raceToDTO(Race raceDbo) {
        return new RaceDto(
                raceDbo.getId(),
                raceDbo.getName(),
                raceDbo.getDate(),
                raceDbo.getNumber(),
                raceDbo.getHorses().stream().map(raceHorse -> new RaceHorseDto(raceHorse.getNoHorse(), raceHorse.getHorse().getId(), raceHorse.getHorse().getName())).toList());
    }
}
