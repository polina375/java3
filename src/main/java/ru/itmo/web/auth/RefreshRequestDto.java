package ru.itmo.web.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(

        @NotBlank(message = "Refresh-токен не может быть пустым")
        String refreshToken

) {
}