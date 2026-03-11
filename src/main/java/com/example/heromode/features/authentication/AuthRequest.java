package com.example.heromode.features.authentication;

public record AuthRequest(
        String email,
        String password,
        String name,
        String birthdate
) {
}
