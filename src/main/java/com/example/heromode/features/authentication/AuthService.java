package com.example.heromode.features.authentication;


import com.example.heromode.features.progression.Player;
import com.example.heromode.features.progression.PlayerRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UserRepository userRepository,
                       PlayerRepository playerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       GoogleTokenVerifier googleTokenVerifier) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    public AuthResponse register(AuthRequest request){
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setBirthdate(request.birthdate());
        User saved = userRepository.save(user);

        Player player = new Player();
        player.setName(saved.getName() != null ? saved.getName(): "Herói");
        player.setUserId(saved.getId());
        playerRepository.save(player);

        String token = jwtService.generateToken(saved.getEmail(), saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getEmail());

    }

    public AuthResponse login(AuthRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() == null) {
            // User originally signed up via Google — refuse password login and
            // tell them to use the Google button instead.
            throw new RuntimeException("Use Google login for this account");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    /**
     * Exchange a Google ID token for our own JWT. The ID token is verified
     * against Google's public keys (signature, issuer, audience, expiration).
     * If the email already has a local account it is linked to the Google
     * identity; otherwise a new User + Player is created on the fly.
     */
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        if (request == null || request.idToken() == null || request.idToken().isBlank()) {
            throw new RuntimeException("Missing Google ID token");
        }

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.idToken());
        if (payload == null) {
            throw new RuntimeException("Invalid Google token");
        }

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // Find user: by googleId first, then by email (link existing account).
        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(email))
                .orElse(null);

        if (user == null) {
            // Fresh signup via Google.
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email);
            user.setGoogleId(googleId);
            // password stays null
            user = userRepository.save(user);

            Player player = new Player();
            player.setName(user.getName() != null ? user.getName() : "Herói");
            player.setUserId(user.getId());
            playerRepository.save(player);
        } else if (user.getGoogleId() == null) {
            // Link existing email-account to this Google identity.
            user.setGoogleId(googleId);
            user = userRepository.save(user);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

}
