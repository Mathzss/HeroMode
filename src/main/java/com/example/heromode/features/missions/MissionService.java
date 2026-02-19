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

        // Calcula XP baseado na dificuldade
        int xp = switch (request.difficulty()) {
            case "Easy"   -> 40;
            case "Medium" -> 100;
            case "Hard"   -> 1000;
            default       -> 0;
        };
        mission.setXpValue(xp);

        return repository.save(mission);
    }

    public List<Mission> listAll() {
        return repository.findAll();
    }

    public void deleteMission(Long id) {
        repository.deleteById(id);
    }
}