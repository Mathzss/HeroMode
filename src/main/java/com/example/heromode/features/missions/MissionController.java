package com.example.heromode.features.missions;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
//@CrossOrigin(origins = "*")
public class MissionController {

    private final MissionService service;

    public MissionController(MissionService service) {
        this.service = service;
    }

    @PostMapping
    public Mission add(@RequestBody MissionRequest request) {
        return service.createMission(request);
    }

    @GetMapping
    public List<Mission> list() {
        return service.listAll();
    }

    @DeleteMapping("/{id}")
    public void complete(@PathVariable Long id) {
        service.deleteMission(id);
    }
}