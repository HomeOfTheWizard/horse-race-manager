package com.homeofthewizard.horseracemanager.service;

import com.homeofthewizard.horseracemanager.dto.CreateRaceDto;
import com.homeofthewizard.horseracemanager.dto.RaceHorseDto;
import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    public void shouldStore_ARace_WithMoreThan2Horses(){
        var race = new CreateRaceDto( "legends", LocalDate.now(), 1, List.of(
                new RaceHorseDto(null, null, "lucky"),
                new RaceHorseDto(null, null, "billy"),
                new RaceHorseDto(null, null, "shadowrun")
        ));

        raceService.save(race);

        var racesStored = raceRepository.findAll();
        assertEquals(1, racesStored.size());
    }

    @Test
    public void shouldNotStore_ARace_WithLessThan3Horses(){
        var race = new CreateRaceDto( "legends", LocalDate.now(), 1, List.of(new RaceHorseDto(null, null, "lucky")));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> raceService.save(race),
                "Expected save to throw RuntimeException, but it did not."
        );

        assertTrue(thrown.getMessage().contains("A race cannot include less than 3 horses"));
    }

    @Test
    public void shouldNotStore_ARace_WithAlreadyExistingSameDateAndName(){
        var race = new Race(null, "legends", LocalDate.now(), 1, List.of());
        raceRepository.save(race);

        var race2 = new CreateRaceDto( "legends", LocalDate.now(), 2, List.of(
                new RaceHorseDto(null, null, "lucky"),
                new RaceHorseDto(null, null, "billy"),
                new RaceHorseDto(null, null, "shadowrun")
        ));

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> raceService.save(race2),
                "Expected save to throw DataIntegrityViolationException, but it did not."
        );

        assertTrue(thrown.getMessage().contains("uniquedateandname"));
    }

    @Test
    public void shouldNotStore_ARace_WithAlreadyExistingSameDateAndNumber(){
        var race = new Race(null, "legends", LocalDate.now(), 1, List.of());
        raceRepository.save(race);

        var race2 = new CreateRaceDto( "masters", LocalDate.now(), 1, List.of(
                new RaceHorseDto(null, null, "lucky"),
                new RaceHorseDto(null, null, "billy"),
                new RaceHorseDto(null, null, "shadowrun")
        ));

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> raceService.save(race2),
                "Expected save to throw DataIntegrityViolationException, but it did not."
        );

        assertTrue(thrown.getMessage().contains("uniquedateandnumber"));
    }

    @Test
    @Transactional
    public void shouldStore_ARace_WithUnorderedHorses_Reorganised(){
        var race = new CreateRaceDto( "legends", LocalDate.now(), 1, List.of(
                new RaceHorseDto(null, null, "lucky"),
                new RaceHorseDto(null, null, "billy"),
                new RaceHorseDto(null, null, "shadowrun")
        ));

        raceService.save(race);

        var racesStored = raceRepository.findAll();
        assertEquals(1, racesStored.size());
        assertEquals(1, (int) racesStored.get(0).getHorses().get(0).getNoHorse());
        assertEquals(2, (int) racesStored.get(0).getHorses().get(1).getNoHorse());
        assertEquals(3, (int) racesStored.get(0).getHorses().get(2).getNoHorse());
    }

    @Test
    @Transactional
    public void shouldSignUp_Horse_To_ARace(){
        raceRepository.deleteAll();
        var race = new Race(null, "legends", LocalDate.now(), 1, List.of());
        raceRepository.save(race);
        var horse = new Horse(null, "shadowrun", Set.of());
        horseRepository.save(horse);

        raceService.signUp(horse.getId(), race.getId());

        var raceUpdated = raceRepository.findById(race.getId()).orElseThrow();
        Assertions.assertEquals(1, raceUpdated.getHorses().size() );
    }
}
