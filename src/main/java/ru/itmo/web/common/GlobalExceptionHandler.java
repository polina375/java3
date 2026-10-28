package ru.itmo.web.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.itmo.web.student.DuplicateStudentException;
import ru.itmo.web.student.StudentNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404: студент не найден
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            StudentNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    // 409: дубликат ИСУ ID (ошибка покажется под полем ИСУ ID)
    @ExceptionHandler(DuplicateStudentException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicate(
            DuplicateStudentException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Студент не сохранён",
                Map.of("isuId", ex.getMessage()), request);
    }

    // 409: нарушено ограничение базы
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT,
                "Данные нарушают ограничения базы (возможно, дубликат)", null, request);
    }

    // 400: не прошла валидация (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> details.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Ошибка валидации", details, request);
    }

    // 400: неверный JSON, дата или неизвестное поле
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleBadJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Некорректное тело запроса (проверьте формат JSON и значения полей)", null, request);
    }

    // 400: неверный тип параметра (например, id=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Неверное значение параметра: " + ex.getName(), null, request);
    }

    // 400: неверные параметры сортировки и пагинации
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, message, null, request);
    }
    // 500: всё остальное
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOther(
            Exception ex, HttpServletRequest request) {
        log.error("Необработанная ошибка: {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", null, request);
    }

    // Собирает ответ об ошибке в формате, который ждёт фронтенд
    private ResponseEntity<ErrorResponseDto> build(
            HttpStatus status, String message, Map<String, String> details,
            HttpServletRequest request) {
        log.warn("{} {} -> {}: {}", request.getMethod(), request.getRequestURI(),
                status.value(), message);
        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(
                        new ErrorResponseDto.ErrorBody(status.value(), message, details)));
    }
}