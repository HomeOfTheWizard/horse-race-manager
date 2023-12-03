package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class HorseService {

    private static final String HORSE_TOPIC = "horse-topic";

    private HorseRepository repository;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public List<Horse> findAll(){
        return repository.findAll();
    }

    @Transactional
    public void save(Horse horse) {
        repository.save(horse);
        try{
            var result = kafkaTemplate.send(HORSE_TOPIC, horse).get();
            log.info("race stored in DB and sent to Kafka: " + result.getRecordMetadata());
        }catch (InterruptedException | ExecutionException ex ){
            log.error("could not send race to kafka topic", ex);
            throw new RuntimeException(ex);
        }
    }
}
