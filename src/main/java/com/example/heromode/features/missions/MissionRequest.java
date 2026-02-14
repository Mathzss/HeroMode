package com.example.heromode.features.missions;

public record MissionRequest(
        String title,
        String category,
        String difficulty
) {}