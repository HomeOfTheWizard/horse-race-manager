package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.entity.RaceHorse;
import com.homeofthewizard.horseracemanager.entity.RaceHorseKey;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceHorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class RaceService {

    private static final String RACE_TOPIC = "race-topic";

    private RaceRepository repository;
    private HorseRepository horseRepository;
    private RaceHorseRepository raceHorseRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public List<Race> findAll(){
        return repository.findAll();
    }

    @Transactional
    public void save(Race race) {
        repository.save(organiseRace(race));
        try{
            var result = kafkaTemplate.send(RACE_TOPIC, race).get();
            log.info("race stored in DB and sent to Kafka: " + result.getRecordMetadata());
        }catch (InterruptedException | ExecutionException ex ){
            log.error("could not send race to kafka topic", ex);
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public Race signUp(Long horseId, Long raceId){
        var race = repository.findById(raceId).orElseThrow(()->new NoSuchElementException("no race with specified Id has been found"));
        var horse = horseRepository.findById(horseId).orElseThrow(()->new NoSuchElementException("no horse with specified Id has been found"));

        var horses = new ArrayList<>(race.getHorses());
        if (horses.stream().map(RaceHorse::getHorse).anyMatch(horse::equals))
            return race;
        else {
            var raceHorse = new RaceHorse(new RaceHorseKey(raceId, horseId), horse, race, horses.size() + 1);
            raceHorseRepository.save(raceHorse);
            horses.add(raceHorse);
            race.setHorses(horses);
            return repository.save(race);
        }
    }

    private Race organiseRace(Race race) {
        var horses = new ArrayList<>(race.getHorses());
        if(horses.size()<3) throw new RuntimeException("A race cannot include less than 3 horses");
        for (int i=0; i<horses.size(); i++) {
            horses.get(i).setNoHorse(i+1);
        }
        race.setHorses(horses);
        return race;
    }
}
