package com.example.heromode.features.authentication;

/**
 * Body sent by the frontend after Google Identity Services returns
 * a credential to the browser. We only need the raw ID token —
 * everything else (email, name, sub) we extract by verifying it
 * against Google's public keys server-side.
 */
public record GoogleAuthRequest(String idToken) {}
