package ru.itmo.web.auth;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}