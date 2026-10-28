package ru.itmo.web.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ru.itmo.web.security.JwtService;
import ru.itmo.web.user.User;
import ru.itmo.web.user.UserRepository;
import ru.itmo.web.user.UserService;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        User user = userService.createUser(
                request.username(),
                request.email(),
                request.password()
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Неверный логин или пароль"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный логин или пароль"
            );
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refresh(RefreshRequestDto request) {

        String oldRefreshToken = request.refreshToken();

        String username = jwtService.validateRefreshToken(oldRefreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Пользователь не найден"
                ));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }
}