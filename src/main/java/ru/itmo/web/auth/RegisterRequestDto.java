package ru.itmo.web.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @NotBlank(message = "Логин не может быть пустым")
        @Size(min = 3, max = 50,
                message = "Логин должен содержать от 3 до 50 символов")
        String username,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8,
                message = "Пароль должен содержать минимум 8 символов")
        String password

) {
}