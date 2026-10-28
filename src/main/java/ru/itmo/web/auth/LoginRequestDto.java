package ru.itmo.web.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

        @NotBlank(message = "Логин не может быть пустым")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        String password

) {
}