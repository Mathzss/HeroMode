package com.example.heromode.features.authentication;

public record AuthResponse (String token, Long userId, String email) {

}
