package com.example.heromode.features.godmode;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByPlayerIdAndGeneratedDate(Long playerId, LocalDate date);

    List<Challenge> findByPlayerIdAndGeneratedDateAndCompleted(
            Long playerId, LocalDate date, Boolean completed);
}
