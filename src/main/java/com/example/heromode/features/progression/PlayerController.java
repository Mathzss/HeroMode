package com.example.heromode.features.progression;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*") // Para o Victor nao ter erro de CORS no React
public class PlayerController {

    private final PlayerRepository repository;

    public PlayerController(PlayerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Player> getPlayers() {
        return repository.findAll();
    }

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return repository.save(player);
    }
}