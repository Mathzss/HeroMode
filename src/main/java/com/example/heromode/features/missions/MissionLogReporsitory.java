package com.example.heromode.features.missions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface MissionLogReporsitory extends JpaRepository<MissionLog, Long> {

    List<MissionLog> findByPlayerIdAndDate(Long playerId, Date date);
    List<MissionLog> findByPlayerIdAndDateAndCompleted(
            Long playerId, LocalDate date, Boolean completed);


}
