package com.example.heromode.features.player;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    public long getXpNeeded(int level) {
        return (long) (100 * Math.pow(level, 1.5));
    }

    public void addXp(Player player, int amount) {
        player.setXp(player.getXp() + amount);

        while (player.getXp() >= getXpNeeded(player.getLevel())) {
            player.setXp(player.getXp() - getXpNeeded(player.getLevel()));
            player.setLevel(player.getLevel() + 1);
            // Aqui você pode disparar a lógica de ganhar um item aleatório
        }
    }
}