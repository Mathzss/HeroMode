package com.example.heromode.features.godmode;

import com.example.heromode.features.missions.MissionLog;
import com.example.heromode.features.missions.MissionLogReporsitory;
import com.example.heromode.features.progression.Player;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MissionLogReporsitory missionLogRepository;

    private static final Map<String, List<String>> TEMPLATES = Map.of(
        "Academia", List.of(
            "Faça 30 flexões",
            "Complete 30 abdominais e 20 polichinelos",
            "Treine por 45 minutos sem parar",
            "Faça 20 burpees",
            "Complete 3 séries de agachamento"
        ),
        "Estudo", List.of(
            "Estude por 30 minutos sem distrações",
            "Complete um módulo ou lição",
            "Leia por 20 minutos",
            "Revise suas anotações de hoje",
            "Complete 2 sessões Pomodoro"
        ),
        "Saúde", List.of(
            "Beba 2L de água hoje",
            "Medite por 10 minutos",
            "Faça uma caminhada de 20 minutos",
            "Durma antes das 23h",
            "Evite açúcar por 24 horas"
        ),
        "Trabalho", List.of(
            "Complete sua tarefa mais importante do dia",
            "Organize sua área de trabalho",
            "Elimine 3 fontes de distração",
            "Planeje as tarefas de amanhã ainda hoje"
        ),
        "Outros", List.of(
            "Aprenda algo novo hoje",
            "Ajude uma pessoa",
            "Saia da zona de conforto em algo pequeno",
            "Pratique gratidão por 5 minutos"
        )
    );

    public ChallengeService(ChallengeRepository challengeRepository,
                            MissionLogReporsitory missionLogRepository) {
        this.challengeRepository = challengeRepository;
        this.missionLogRepository = missionLogRepository;
    }

    // Idempotent — safe to call multiple times per day
    public void generateDailyChallenges(Player player) {
        LocalDate today = LocalDate.now();

        List<Challenge> existing = challengeRepository
                .findByPlayerIdAndGeneratedDate(player.getId(), today);
        if (!existing.isEmpty()) return;

        LocalDate thirtyDaysAgo = today.minusDays(30);
        List<MissionLog> history = missionLogRepository
                .findByPlayerIdAndCompletedAndDateBetween(
                        player.getId(), true, thirtyDaysAgo, today);

        String topCategory = findTopCategory(history);

        Random random = new Random();
        List<Challenge> challenges = new ArrayList<>();

        List<String> topTemplates = new ArrayList<>(TEMPLATES.get(topCategory));
        Collections.shuffle(topTemplates, random);
        for (int i = 0; i < Math.min(2, topTemplates.size()); i++) {
            challenges.add(buildChallenge(player, topCategory, topTemplates.get(i), today));
        }

        List<String> otherCategories = new ArrayList<>(TEMPLATES.keySet());
        otherCategories.remove(topCategory);
        String randomCat = otherCategories.get(random.nextInt(otherCategories.size()));
        List<String> randomTemplates = new ArrayList<>(TEMPLATES.get(randomCat));
        Collections.shuffle(randomTemplates, random);
        challenges.add(buildChallenge(player, randomCat, randomTemplates.get(0), today));

        challengeRepository.saveAll(challenges);
    }

    public List<Challenge> getTodayChallenges(Long playerId) {
        return challengeRepository.findByPlayerIdAndGeneratedDateAndCompleted(
                playerId, LocalDate.now(), false);
    }

    public int completeChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Desafio não encontrado"));
        challenge.setCompleted(true);
        challengeRepository.save(challenge);
        return challenge.getXpBonus();
    }

    private String findTopCategory(List<MissionLog> logs) {
        if (logs.isEmpty()) return "Outros";
        return logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getMission().getCategory(),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Outros");
    }

    private Challenge buildChallenge(Player player, String category, String title, LocalDate date) {
        Challenge c = new Challenge();
        c.setPlayer(player);
        c.setTitle(title);
        c.setCategory(category);
        c.setXpBonus(200);
        c.setGeneratedDate(date);
        c.setCompleted(false);
        return c;
    }
}
