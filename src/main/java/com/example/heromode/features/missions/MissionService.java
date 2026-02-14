package com.example.heromode.features.missions;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionService {

    private final MissionRepository repository;

    public MissionService(MissionRepository repository) {
        this.repository = repository;
    }

    public Mission createMission(MissionRequest request) {
        Mission mission = new Mission();
        mission.setTitle(request.title());
        mission.setCategory(request.category());
        mission.setDifficulty(request.difficulty());

        // Mantendo a lógica de XP que vimos no App.jsx
        int xp = switch (request.difficulty()) {
            case "Hard" -> 1000;
            case "Medium" -> 100;
            default -> 40;
        };
        mission.setXpValue(xp);

        return repository.save(mission);
    }

    public List<Mission> listAll() {
        return repository.findAll();
    }
}