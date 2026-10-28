package ru.itmo.web.student;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentPatchRequestDto(

        @Pattern(regexp = ".*\\S.*", message = "ФИО не может быть пустым")
        @Pattern(regexp = "^\\D*$", message = "ФИО не должно содержать цифры")
        String fullName,

        @Pattern(regexp = "^\\p{L}\\d{4}$",
                message = "Группа должна иметь формат: 1 буква + 4 цифры")
        String groupName,

        @Null(message = "Это поле является идентификатором и не может быть изменено")
        String isuId,

        @Positive(message = "Номер общежития должен быть положительным")
        Integer dormNumber,

        @Pattern(regexp = "^[1-9]\\d*$",
                message = "Номер комнаты должен быть положительным целым числом")
        String room,

        LocalDate moveInDate,

        Boolean isForeigner,

        @Size(max = 2000, message = "Заметки не длиннее 2000 символов")
        String notes
) {
}