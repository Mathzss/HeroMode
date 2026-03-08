package com.example.heromode.features.authentication;


import com.example.heromode.features.progression.Player;
import com.example.heromode.features.progression.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PlayerRepository playerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

    }

    public AuthResponse register(AuthRequest request){
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);

        Player player = new Player();
        player.setName("Herói");
        player.setUserId(saved.getId());
        playerRepository.save(player);

        String token = jwtService.generateToken(saved.getEmail(), saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getEmail());

    }

    public AuthResponse login(AuthRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

}
