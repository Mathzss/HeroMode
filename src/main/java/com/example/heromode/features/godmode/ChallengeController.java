package com.example.heromode.features.godmode;

import com.example.heromode.features.progression.Player;
import com.example.heromode.features.progression.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

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
            challengeService.generateDailyChallenges(player);
            return challengeService.getTodayChallenges(player.getId());
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
