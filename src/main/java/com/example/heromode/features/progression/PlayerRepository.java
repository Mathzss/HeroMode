package com.example.heromode.features.progression;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Aqui voce ja tem save, findById, findAll, etc.
}