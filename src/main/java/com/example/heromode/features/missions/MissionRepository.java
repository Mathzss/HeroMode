package com.example.heromode.features.missions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    // No futuro, podemos filtrar missões por usuário aqui
}