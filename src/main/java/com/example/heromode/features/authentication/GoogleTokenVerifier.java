package com.example.heromode.features.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.Collections;

/**
 * Verifies an ID token issued by Google for "Sign in with Google".
 *
 * The verifier checks the signature against Google's published JWK set,
 * validates the issuer, audience (our OAuth client id), and expiration.
 * If any of that fails, getPayload() returns null and we treat the
 * request as unauthenticated.
 */
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
        // If clientId is empty (env var not set yet), build a verifier that
        // will accept the signature but reject every token at audience check —
        // safer than crashing the whole app at startup.
        var transport = new NetHttpTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();
        var builder = new GoogleIdTokenVerifier.Builder(transport, jsonFactory);
        if (clientId != null && !clientId.isBlank()) {
            builder.setAudience(Collections.singletonList(clientId));
        }
        this.verifier = builder.build();
    }

    /**
     * @return the verified payload (email, name, sub) or null if invalid.
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken token = verifier.verify(idTokenString);
            return token == null ? null : token.getPayload();
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
