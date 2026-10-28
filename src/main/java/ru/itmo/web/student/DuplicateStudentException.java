package ru.itmo.web.student;

public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String isuId) {
        super("Студент с ИСУ ID " + isuId + " уже существует");
    }
}