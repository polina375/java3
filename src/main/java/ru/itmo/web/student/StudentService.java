package ru.itmo.web.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // ---------- Получение ----------

    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAll() {
        return repository.findAll().stream()
                .map(StudentResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getById(Long id) {
        return StudentResponseDto.from(findStudent(id));
    }

    // ---------- Создание ----------

    @Transactional
    public StudentResponseDto create(StudentRequestDto dto) {
        checkUniqueIsu(dto.isuId(), null);

        Student student = new Student();
        copyFields(dto, student);

        Student saved = repository.save(student);
        log.info("Создан студент id={}, isuId={}", saved.getId(), saved.getIsuId());
        return StudentResponseDto.from(saved);
    }

    // ---------- Полное изменение (PUT) ----------

    @Transactional
    public StudentResponseDto update(Long id, StudentRequestDto dto) {
        Student student = findStudent(id);
        checkUniqueIsu(dto.isuId(), id);

        copyFields(dto, student);

        Student saved = repository.save(student);
        log.info("Обновлён студент id={}", id);
        return StudentResponseDto.from(saved);
    }

    // ---------- Частичное изменение (PATCH) ----------

    @Transactional
    public StudentResponseDto patch(Long id, StudentPatchRequestDto dto) {
        Student student = findStudent(id);

        // Меняем только те поля, которые пришли (не null)
        if (dto.fullName() != null)    student.setFullName(dto.fullName().trim());
        if (dto.groupName() != null)   student.setGroupName(dto.groupName().trim());
        if (dto.dormNumber() != null)  student.setDormNumber(dto.dormNumber());
        if (dto.room() != null)        student.setRoom(dto.room());
        if (dto.moveInDate() != null)  student.setMoveInDate(dto.moveInDate());
        if (dto.isForeigner() != null) student.setForeigner(dto.isForeigner());
        if (dto.notes() != null)       student.setNotes(dto.notes());

        Student saved = repository.save(student);
        log.info("Частично обновлён студент id={}", id);
        return StudentResponseDto.from(saved);
    }

    // ---------- Удаление ----------

    @Transactional
    public void delete(Long id) {
        Student student = findStudent(id);
        repository.delete(student);
        log.info("Удалён студент id={}", id);
    }

    // ---------- Поиск, фильтры, сортировка, пагинация ----------

    @Transactional(readOnly = true)
    public Page<StudentResponseDto> search(
            String fullName,
            String groupName,
            String isuId,
            Integer dormNumber,
            String room,
            LocalDate moveInDate,
            Boolean isForeigner,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        // Сортировать можно только по этим полям
        List<String> allowed = List.of(
                "id", "fullName", "groupName", "isuId",
                "dormNumber", "room", "moveInDate"
        );

        if (!allowed.contains(sortBy)) {
            throw new IllegalArgumentException("Недопустимое поле сортировки: " + sortBy);
        }
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Некорректные параметры пагинации");
        }

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return repository.findAll(
                StudentSpecifications.withFilters(
                        emptyToNull(fullName),
                        emptyToNull(groupName),
                        emptyToNull(isuId),
                        dormNumber,
                        emptyToNull(room),
                        moveInDate,
                        isForeigner
                ),
                pageable
        ).map(StudentResponseDto::from);
    }

    // ---------- Вспомогательные методы ----------

    // Найти студента или выбросить ошибку 404
    private Student findStudent(Long id) {
        Optional<Student> found = repository.findById(id);
        if (found.isEmpty()) {
            log.warn("Студент id={} не найден", id);
            throw new StudentNotFoundException(id);
        }
        return found.get();
    }

    // Скопировать поля из запроса в сущность
    private void copyFields(StudentRequestDto dto, Student student) {
        student.setFullName(dto.fullName().trim());
        student.setGroupName(dto.groupName().trim());
        student.setIsuId(dto.isuId());
        student.setDormNumber(dto.dormNumber());
        student.setRoom(dto.room());
        student.setMoveInDate(dto.moveInDate());
        student.setForeigner(dto.isForeigner());
        student.setNotes(dto.notes());
    }

    // Проверить, что такого ИСУ ID нет у другого студента
    private void checkUniqueIsu(String isuId, Long currentId) {
        Optional<Student> existing = repository.findByIsuId(isuId);
        if (existing.isPresent() && !existing.get().getId().equals(currentId)) {
            log.warn("Дубликат ИСУ ID: {}", isuId);
            throw new DuplicateStudentException(isuId);
        }
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}