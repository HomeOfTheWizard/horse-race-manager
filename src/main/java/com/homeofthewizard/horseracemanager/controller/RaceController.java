package com.homeofthewizard.horseracemanager.controller;

import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.service.RaceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class RaceController {

    private final RaceService service;

    @GetMapping("/api/races")
    public List<Race> getAll() {
        return service.findAll();
    }

    @PostMapping("/api/races")
    public void getAll(List<Race> races) {
        for (var race: races) {
            service.save(race);
        }
    }

    @PostMapping("/api/race")
    public void create(Race race) {
        service.save(race);
    }

    @GetMapping("/api/signup")
    public void signUp(@RequestParam Long horseId, @RequestParam Long raceId) {
        service.signUp(horseId, raceId);
    }
}