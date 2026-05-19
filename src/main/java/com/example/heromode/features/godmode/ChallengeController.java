package com.example.heromode.features.godmode;

import com.example.heromode.features.progression.Player;
import com.example.heromode.features.progression.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    private final ChallengeService challengeService;
    private final PlayerRepository playerRepository;

    public ChallengeController(ChallengeService challengeService,
                               PlayerRepository playerRepository) {
        this.challengeService = challengeService;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Challenge> getTodayChallenges() {
        Long userId = extractUserId();
        Player player = playerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Player não encontrado"));

        if (Boolean.TRUE.equals(player.getGodModeEnabled())) {
            try {
                challengeService.generateDailyChallenges(player);
                return challengeService.getTodayChallenges(player.getId());
            } catch (Exception e) {
                log.error("Erro ao gerar/buscar challenges para player {}: {}", player.getId(), e.getMessage(), e);
                return List.of();
            }
        }
        return List.of();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Integer> completeChallenge(@PathVariable Long id) {
        int xpGained = challengeService.completeChallenge(id);
        return ResponseEntity.ok(xpGained);
    }

    private Long extractUserId() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder
                        .getContext().getAuthentication();
        return (Long) auth.getCredentials();
    }
}
