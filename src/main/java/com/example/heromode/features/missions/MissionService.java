package com.example.heromode.features.missions;

import com.example.heromode.features.progression.Player;
import com.example.heromode.features.progression.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MissionService {

    private final MissionRepository repository;
    private final MissionLogReporsitory logReporsitory;
    private final PlayerRepository playerRepository;

    public MissionService(MissionRepository repository,
                          MissionLogReporsitory logReporsitory,
                          PlayerRepository playerRepository) {
        this.repository = repository;
        this.logReporsitory = logReporsitory;
        this.playerRepository = playerRepository;
    }


    //Função alterada para criar missão template vinculada ao player
    public Mission createMission(MissionRequest request) {
       Player player = playerRepository.findById(request.playerId())
               .orElseThrow(() -> new RuntimeException("Player não encontrado"));

        Mission mission = new Mission();
        mission.setTitle(request.title());
        mission.setCategory(request.category());
        mission.setDifficulty(request.difficulty());
        mission.setPlayer(player);

        // Calcula XP baseado na dificuldade
        int xp = switch (request.difficulty()) {
            case "Easy"   -> 40;
            case "Medium" -> 100;
            case "Hard"   -> 1000;
            default       -> 0;
        };
        mission.setXpValue(xp);

        Mission saved = repository.save(mission);

        //Já cria o log de hoje para a missão.
        createLogForToday(saved, player);

        return saved;
    }

    //Gera logs do dia para todas as missões do player
    public void generateDailyLogs(Player player) {
        LocalDate today = LocalDate.now();
        List<Mission> missions = repository.findByPlayerId(player.getId());

        for (Mission mission : missions){
            boolean logJaExiste = logReporsitory
                    .findByPlayerIdAndDate(player.getId(), today)
                    .stream()
                    .anyMatch(log->log.getMission().getId().equals(mission.getId()));

            if (!logJaExiste){
                createLogForToday(mission, player);
            }
        }
    }

    private void createLogForToday(Mission mission, Player player) {
        MissionLog log = new MissionLog();
        log.setMission(mission);
        log.setPlayer(player);
        log.setDate(LocalDate.now());
        log.setCompleted(false);
        logReporsitory.save(log);
    }

    //Busca logs de hoje do player
    public List<MissionLog> getTodayLogs(Long playerId) {
        return logReporsitory.findByPlayerIdAndDateAndCompleted(
                playerId, LocalDate.now(), false);
    }

    public int completeLog(Long logId){
        MissionLog log = logReporsitory.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log não encontrado"));

        log.setCompleted(true);
        logReporsitory.save(log);

        return log.getMission().getXpValue();
    }

    //Chama no login e penaliza por missões não feitas
    public boolean applyPenaltyIfNeeded(Player player) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        //Só vai penalizar se o lastlogin foi antes de hoje
        if (player.getLastLogin() == null ||
             player.getLastLogin().isBefore(LocalDate.now())) {

            List<MissionLog> missed = logReporsitory
                    .findByPlayerIdAndDateAndCompleted(
                            player.getId(), yesterday, false
                    );

            if (!missed.isEmpty()) {
                long penalty = missed.size() * 50L;
                player.setXp(Math.max(0, player.getXp() - penalty));
                player.setStreak(0);
                playerRepository.save(player);
                return true;
            }
        }
        return false;
    }

    public List<Mission> listAll(){
        return repository.findAll();
    }

    public void deleteMission(Long id){
        repository.deleteById(id);
    }

}