package ru.itmo.web.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    // Найти студента по ИСУ ID (нужно для проверки дубликатов)
    Optional<Student> findByIsuId(String isuId);
}