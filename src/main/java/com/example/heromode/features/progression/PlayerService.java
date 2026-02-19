package com.example.heromode.features.progression;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
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