package com.example.heromode.features.progression;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/player")
//@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerRepository repository;
    private final PlayerService service;

    public PlayerController(PlayerRepository repository, PlayerService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<PlayerLoginResponse> getPlayers() {
        return repository.findAll()
                .stream()
                .map(p -> service.login(p.getId()))
                .toList();
    }

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return repository.save(player);
    }


    @PutMapping("/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return service.updatePlayer(id, player);
    }
}