package ru.itmo.web.student;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class StudentSpecifications {

    public static Specification<Student> withFilters(
            String fullName,
            String groupName,
            String isuId,
            Integer dormNumber,
            String room,
            LocalDate moveInDate,
            Boolean isForeigner
    ) {
        return (root, query, cb) -> {

            var predicates = new java.util.ArrayList<
                    jakarta.persistence.criteria.Predicate>();

            if (fullName != null && !fullName.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("fullName")),
                                "%" + fullName.toLowerCase() + "%"
                        )
                );
            }

            if (groupName != null && !groupName.isBlank()) {
                predicates.add(
                        cb.equal(root.get("groupName"), groupName)
                );
            }

            if (isuId != null && !isuId.isBlank()) {
                predicates.add(
                        cb.equal(root.get("isuId"), isuId)
                );
            }

            if (dormNumber != null) {
                predicates.add(
                        cb.equal(root.get("dormNumber"), dormNumber)
                );
            }

            if (room != null && !room.isBlank()) {
                predicates.add(
                        cb.equal(root.get("room"), room)
                );
            }

            if (moveInDate != null) {
                predicates.add(
                        cb.equal(root.get("moveInDate"), moveInDate)
                );
            }

            if (isForeigner != null) {
                predicates.add(
                        cb.equal(root.get("isForeigner"), isForeigner)
                );
            }

            return cb.and(predicates.toArray(
                    new jakarta.persistence.criteria.Predicate[0]
            ));
        };
    }
}
