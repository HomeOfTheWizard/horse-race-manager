package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.entity.RaceHorse;
import com.homeofthewizard.horseracemanager.entity.RaceHorseKey;
import com.homeofthewizard.horseracemanager.repository.RaceRepository;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
public class RaceServiceTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3"));

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        kafka.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        kafka.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    RaceRepository raceRepository;

    @Autowired
    HorseRepository horseRepository;

    @Autowired
    RaceService raceService;

    @BeforeEach
    void setUp() {
        raceRepository.deleteAll();
    }

    @Test
    public void shouldStoreARaceWithMoreThan2Horses(){
        var race = new Race(null, LocalDate.now(), "legends", 1, List.of());
        var horse1 = new Horse(null, "shadowrun", Set.of());
        var horse2 = new Horse(null, "billy", Set.of());
        var horse3 = new Horse(null, "whiteFang", Set.of());
        var raceHorse1 = new RaceHorse(new RaceHorseKey(null,null), horse1, race, null );
        var raceHorse2 = new RaceHorse(new RaceHorseKey(null,null), horse2, race, null );
        var raceHorse3 = new RaceHorse(new RaceHorseKey(null,null), horse3, race, null );
        race.setHorses(List.of(raceHorse1, raceHorse2, raceHorse3));
        horse1.setRaces(Set.of(raceHorse1));
        horse2.setRaces(Set.of(raceHorse2));
        horse3.setRaces(Set.of(raceHorse3));

        raceService.save(race);

        var racesStored = raceRepository.findAll();
        assertTrue(racesStored.size() == 1);
    }

    @Test
    public void shouldNotStoreARaceWithLessThan3Horses(){
        var race = new Race(null, LocalDate.now(), "legends", 1, List.of());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> raceService.save(race),
                "Expected save to throw RuntimeException, but it did not."
        );

        assertTrue(thrown.getMessage().contains("A race cannot include less than 3 horses"));
    }

    @Test
    @Transactional
    public void shouldStoreARaceWithUnorderedHorsesReorganised(){
        var race = new Race(null, LocalDate.now(), "legends", 1, List.of());
        var horse1 = new Horse(null, "shadowrun", Set.of());
        var horse2 = new Horse(null, "billy", Set.of());
        var horse3 = new Horse(null, "whiteFang", Set.of());
        var raceHorse1 = new RaceHorse(new RaceHorseKey(null,null), horse1, race, 2 );
        var raceHorse2 = new RaceHorse(new RaceHorseKey(null,null), horse2, race, 4 );
        var raceHorse3 = new RaceHorse(new RaceHorseKey(null,null), horse3, race, 1 );
        race.setHorses(List.of(raceHorse1, raceHorse2, raceHorse3));
        horse1.setRaces(Set.of(raceHorse1));
        horse2.setRaces(Set.of(raceHorse2));
        horse3.setRaces(Set.of(raceHorse3));

        raceService.save(race);

        var racesStored = raceRepository.findAll();
        assertTrue(racesStored.size() == 1);
        assertTrue(racesStored.get(0).getHorses().get(0).getNoHorse().equals(1));
        assertTrue(racesStored.get(0).getHorses().get(1).getNoHorse().equals(2));
        assertTrue(racesStored.get(0).getHorses().get(2).getNoHorse().equals(3));
    }

    @Test
    @Transactional
    public void shouldSignUpHorseToARace(){
        raceRepository.deleteAll();
        var race = new Race(null, LocalDate.now(), "legends", 1, List.of());
        raceRepository.save(race);
        var horse = new Horse(null, "shadowrun", Set.of());
        horseRepository.save(horse);

        raceService.signUp(horse.getId(), race.getId());

        var raceUpdated = raceRepository.findById(race.getId()).orElseThrow();
        Assertions.assertEquals(1, raceUpdated.getHorses().size() );
    }
}
