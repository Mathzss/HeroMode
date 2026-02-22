package com.example.heromode.features.progression;

import com.example.heromode.features.missions.MissionLog;
import com.example.heromode.features.missions.MissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final MissionService missionService;

    public PlayerService(PlayerRepository repository,
                         MissionService missionService) {
        this.repository = repository;
        this.missionService = missionService;
    }

    //Chama quando o front faz GET do player
    public PlayerLoginResponse login(Long playerId){
        Player player = repository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player não encontrado"));

        // Aplica a penalidade
        boolean penalized = missionService.applyPenaltyIfNeeded(player);

        // Gera Logs do dia para as missões do player
        missionService.generateDailyLogs(player);

        // Atualiza LastLogin
        player.setLastLogin(LocalDate.now());
        repository.save(player);

        // Busca missões de hoje
        List<MissionLog> todayLogs = missionService.getTodayLogs(playerId);

        return new PlayerLoginResponse(player, todayLogs, penalized);
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
}