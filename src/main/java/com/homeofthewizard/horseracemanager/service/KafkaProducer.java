package com.homeofthewizard.horseracemanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendToKafkaTopic(Object object, String topic) {
        try{
            var result = kafkaTemplate.send(topic, object).get();
            log.info("object sent to Kafka: " + result.getRecordMetadata());
        }catch (InterruptedException | ExecutionException ex ){
            log.error("could not send race to kafka topic", ex);
            throw new RuntimeException(ex);
        }
    }
}
