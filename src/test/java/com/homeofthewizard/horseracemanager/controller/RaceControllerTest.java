package com.homeofthewizard.horseracemanager.controller;

import com.homeofthewizard.horseracemanager.dto.CreateRaceDto;
import com.homeofthewizard.horseracemanager.dto.CreateRaceHorseDto;
import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import com.homeofthewizard.horseracemanager.repository.RaceRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.location=classpath:/application-test.yaml")
class RaceControllerTest {

    @LocalServerPort
    private Integer port;

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

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        raceRepository.deleteAll();
        horseRepository.deleteAll();
    }

    @Test
    void shouldGetAllRaces() {
        List<Race> races = List.of(
                new Race(null, "noobs", LocalDate.now(), 1, List.of()),
                new Race(null, "legends", LocalDate.now(), 2, List.of())
        );
        raceRepository.saveAll(races);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/races")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

    @Test
    void shouldPostARace() {
        var race = new CreateRaceDto( "legends", LocalDate.now(), 1, List.of(
                new CreateRaceHorseDto(null,"lucky"),
                new CreateRaceHorseDto(null,"billy"),
                new CreateRaceHorseDto(null,"shadowrun")
        ));

        given()
                .contentType(ContentType.JSON)
                .body(race)
                .when()
                .post("/api/race")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldReturn4XX_PostARace_WithLessThan3Horses() {
        var race = new CreateRaceDto( "legends", LocalDate.now(), 1, List.of(
                new CreateRaceHorseDto(null,"lucky"),
                new CreateRaceHorseDto(null,"shadowrun")
        ));

        given()
                .contentType(ContentType.JSON)
                .body(race)
                .when()
                .post("/api/race")
                .then()
                .statusCode(409);
    }

    @Test
    void shouldSignUpHorseToARace() {
        var race = new Race(null, "legends", LocalDate.now(), 1, List.of());
        raceRepository.save(race);
        var horse = new Horse(null, "shadowrun", Set.of());
        horseRepository.save(horse);

        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("horseId", horse.getId())
                .queryParam("raceId", race.getId())
                .get("/api/signup")
                .then()
                .statusCode(200);
    }
}