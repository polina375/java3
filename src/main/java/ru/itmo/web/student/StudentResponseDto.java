package ru.itmo.web.student;

import java.time.LocalDate;

public record StudentResponseDto(
        Long id,
        String fullName,
        String groupName,
        String isuId,
        Integer dormNumber,
        String room,
        LocalDate moveInDate,
        Boolean isForeigner,
        String notes
) {
    // Превращает сущность Student (из базы) в ответ для клиента
    public static StudentResponseDto from(Student student) {
        return new StudentResponseDto(
                student.getId(),
                student.getFullName(),
                student.getGroupName(),
                student.getIsuId(),
                student.getDormNumber(),
                student.getRoom(),
                student.getMoveInDate(),
                student.getForeigner(),
                student.getNotes()
        );
    }
}