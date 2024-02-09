package com.homeofthewizard.horseracemanager.controller;

import com.homeofthewizard.horseracemanager.dto.HorseDto;
import com.homeofthewizard.horseracemanager.service.HorseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class HorseController {

    private HorseService service;

    @GetMapping(path = "/horses")
    public List<HorseDto> findAll(){
        return service.findAll();
    }

    @PostMapping("/horses")
    public List<HorseDto> create(@RequestBody @Valid List<HorseDto> horses) {
        var persistedHorses = new ArrayList<HorseDto>();
        for (var horse: horses) {
            persistedHorses.add(service.save(horse));
        }
        return persistedHorses;
    }

    @PostMapping("/horse")
    public HorseDto create(@RequestBody @Valid HorseDto horse) {
        return service.save(horse);
    }
}
