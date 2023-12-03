package com.homeofthewizard.horseracemanager.controller;

import com.homeofthewizard.horseracemanager.entity.Horse;
import com.homeofthewizard.horseracemanager.entity.Race;
import com.homeofthewizard.horseracemanager.repository.HorseRepository;
import com.homeofthewizard.horseracemanager.service.HorseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class HorseController {

    private HorseService service;

    @GetMapping(path = "/api/horses")
    public List<Horse> findAll(){
        return service.findAll();
    }

    @PostMapping("/api/horses")
    public void getAll(List<Horse> horses) {
        for (var horse: horses) {
            service.save(horse);
        }
    }

    @PostMapping("/api/horse")
    public void create(Horse horse) {
        service.save(horse);
    }
}
