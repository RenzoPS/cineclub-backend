package com.cineclub.specifications;

import com.cineclub.dtos.ScreeningFilterDto;
import com.cineclub.entities.Screening;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScreeningSpecification {
    public static Specification<Screening> buildFilter(ScreeningFilterDto filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por película (relación ManyToOne)
            if(filter.getMovieId() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("movie").get("id"), filter.getMovieId()
                ));
            }

            // Filtro por nombre de película (contiene, case insensitive)
            if(filter.getMovieName() != null && !filter.getMovieName().isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("movie").get("title")),
                        "%" + filter.getMovieName().toLowerCase() + "%"
                ));
            }

            // Filtro por sala (relación ManyToOne)
            if(filter.getRoomId() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("room").get("id"),
                        filter.getRoomId()
                ));
            }

            // Filtro por número de sala (exacto)
            if(filter.getRoomNumber() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("room").get("number"),
                        filter.getRoomNumber()
                ));
            }

            // Filtro por fecha de inicio específica (mayor o igual)
            if(filter.getStartAt() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startTime"), filter.getStartAt()
                ));
            }

            // Filtro por fecha de fin específica (menor o igual)
            if(filter.getEndAt() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endTime"), filter.getEndAt()
                ));
            }

            // Filtro: solo funciones de hoy
            if(filter.getToday() != null && filter.getToday()){
                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
                
                predicates.add(criteriaBuilder.between(
                        root.get("startTime"), startOfDay, endOfDay
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
