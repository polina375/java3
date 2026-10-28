package ru.itmo.web.common;

import java.util.Map;

public record ErrorResponseDto(ErrorBody error) {

    public record ErrorBody(int status, String message, Map<String, String> details) {
    }
}