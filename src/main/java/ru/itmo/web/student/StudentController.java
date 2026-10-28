package ru.itmo.web.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Студенты", description = "Управление студентами")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @Operation(summary = "Получить всех студентов")
    @GetMapping
    public List<StudentResponseDto> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Получить студента по id")
    @GetMapping("/{id}")
    public StudentResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Создать студента (ADMIN)")
    @PostMapping
    public ResponseEntity<StudentResponseDto> create(
            @Valid @RequestBody StudentRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Полностью заменить студента (ADMIN)")
    @PutMapping("/{id}")
    public StudentResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto dto
    ) {
        return service.update(id, dto);
    }

    @Operation(summary = "Частично изменить студента (ADMIN)")
    @PatchMapping("/{id}")
    public StudentResponseDto patch(
            @PathVariable Long id,
            @Valid @RequestBody StudentPatchRequestDto dto
    ) {
        return service.patch(id, dto);
    }

    @Operation(summary = "Удалить студента (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск, фильтры, сортировка и пагинация")
    @GetMapping("/search")
    public Page<StudentResponseDto> search(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String isuId,
            @RequestParam(required = false) Integer dormNumber,
            @RequestParam(required = false) String room,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate moveInDate,
            @RequestParam(required = false) Boolean isForeigner,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return service.search(
                fullName, groupName, isuId, dormNumber,
                room, moveInDate, isForeigner,
                page, size, sortBy, direction
        );
    }
}