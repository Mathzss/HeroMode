package com.example.heromode.features.progression;

import com.example.heromode.features.godmode.Challenge;
import com.example.heromode.features.godmode.ChallengeService;
import com.example.heromode.features.missions.MissionLog;
import com.example.heromode.features.missions.MissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final MissionService missionService;
    private final ChallengeService challengeService;

    public PlayerService(PlayerRepository repository,
                         MissionService missionService,
                         ChallengeService challengeService) {
        this.repository = repository;
        this.missionService = missionService;
        this.challengeService = challengeService;
    }

    public PlayerLoginResponse login(Long playerId){
        Player player = repository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player não encontrado"));

        boolean penalized = missionService.applyPenaltyIfNeeded(player);
        missionService.generateDailyLogs(player);

        player.setLastLogin(LocalDate.now());
        repository.save(player);

        List<MissionLog> todayLogs = missionService.getTodayLogs(playerId);

        List<Challenge> todayChallenges = List.of();
        if (Boolean.TRUE.equals(player.getGodModeEnabled())) {
            try {
                challengeService.generateDailyChallenges(player);
                todayChallenges = challengeService.getTodayChallenges(player.getId());
            } catch (Exception e) {
                // Não deixa falha de challenges quebrar o login
            }
        }

        return new PlayerLoginResponse(player, todayLogs, penalized, todayChallenges);
    }

    public Player updatePlayer(Long id, Player updated) {
        Player player = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player não encontrado"));
        player.setXp(updated.getXp());
        player.setLevel(updated.getLevel());
        player.setStreak(updated.getStreak());
        player.setName(updated.getName());
        return repository.save(player);
    }

    public Player updateGodMode(Long id, boolean enabled) {
        Player player = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player não encontrado"));
        player.setGodModeEnabled(enabled);
        return repository.save(player);
    }
}