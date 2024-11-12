package com.arcane.dam.controller;

import com.arcane.dam.dto.TeamDTO;
import com.arcane.dam.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/team")
@RequiredArgsConstructor
@CrossOrigin
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/all")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return new ResponseEntity<>(teamService.getAllTeams(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<HttpStatus> updateTeam(@RequestBody TeamDTO teamDTO) {
        return new ResponseEntity<>(teamService.updateTeam(teamDTO) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTeam(@PathVariable String id) {
        return new ResponseEntity<>(teamService.deleteTeam(id) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<TeamDTO> getTeam(@RequestParam String id) {
        return new ResponseEntity<>(teamService.getTeam(id), HttpStatus.OK);
    }
}
