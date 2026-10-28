package ru.itmo.web.student;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("Студент с id " + id + " не найден");
    }
}