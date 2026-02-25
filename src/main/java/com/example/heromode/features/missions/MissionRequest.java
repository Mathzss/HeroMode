package com.example.heromode.features.missions;

public record MissionRequest(
        Long playerId,
        String title,
        String category,
        String difficulty
) {}