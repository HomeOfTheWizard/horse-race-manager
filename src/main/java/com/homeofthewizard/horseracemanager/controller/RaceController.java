package com.homeofthewizard.horseracemanager.controller;

import com.homeofthewizard.horseracemanager.dto.CreateRaceDto;
import com.homeofthewizard.horseracemanager.dto.RaceDto;
import com.homeofthewizard.horseracemanager.dto.RaceInformationDto;
import com.homeofthewizard.horseracemanager.service.RaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class RaceController {

    private final RaceService service;

    @GetMapping("/races")
    public List<RaceDto> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Create a race, with optional the list of horses to sign up. " +
            "If you want to signup an already registered horse, provide its id. If you do so, all changes on the horse's information will be ignored. It will be signed-up with information already registered. " +
            "The horses' race numbers will be assigned in the given order, starting with 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Race created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RaceDto.class)) }),
            @ApiResponse(responseCode = "4XX", description = "Race cannot be created. Required constraints are not respected",
                    content = @Content),
            @ApiResponse(responseCode = "5XX", description = "Race cannot be created. Something went wrong on the server",
                    content = @Content)})
    @PostMapping("/race")
    public RaceDto create(@RequestBody @Valid CreateRaceDto race) {
        return service.save(race);
    }

    @Operation(summary = "Update a race information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Race created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "4XX", description = "Race cannot be updated. Required constraints are not respected",
                    content = @Content),
            @ApiResponse(responseCode = "5XX", description = "Race cannot be updated. Something went wrong on the server",
                    content = @Content)})
    @PutMapping("/race")
    public RaceDto update(@RequestBody @Valid RaceInformationDto race) {
        return service.update(race);
    }

    @PostMapping("/races")
    public List<RaceDto> create(@RequestBody @Valid List<CreateRaceDto> races) {
        var persistedRaces = new ArrayList<RaceDto>();
        for (var race: races) {
            service.save(race);
        }
        return persistedRaces;
    }

    @Operation(summary = "Sign up a horse to a race")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse signed up",
                    content = { @Content }),
            @ApiResponse(responseCode = "500", description = "Could not sign up the horse",
                    content = @Content) })
    @GetMapping("/signup")
    public RaceDto signUp(@RequestParam Long horseId, @RequestParam Long raceId) {
        return service.signUp(horseId, raceId);
    }

    @Operation(summary = "Drop out a horse of a race")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse dropped out",
                    content = { @Content }),
            @ApiResponse(responseCode = "4XX", description = "Could not drop out the horse",
                    content = @Content) })
    @GetMapping("/dropout")
    public RaceDto dropOut(@RequestParam Long horseId, @RequestParam Long raceId) {
        return service.dropOut(horseId, raceId);
    }
}