package com.cineclub.specifications;

import com.cineclub.dtos.MovieFilterDto;
import com.cineclub.entities.Movie;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecification {

    public static Specification<Movie> buildFilter(MovieFilterDto filter){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por titulo (contiene, case insensitive)
            if (filter.getTitle() != null && !filter.getTitle().isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }

            // Filtro por género (exacto)
            if (filter.getGenre() != null && !filter.getGenre().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("genre"), filter.getGenre()
                ));
            }

            // Filtro por rating exacto (tiene prioridad sobre rango)
            if (filter.getRating() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("rating"), filter.getRating()
                ));
            } else {
                // Filtro por rating mínimo
                if (filter.getMinRating() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                            root.get("rating"), filter.getMinRating()
                    ));
                }

                // Filtro por rating máximo
                if (filter.getMaxRating() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                            root.get("rating"), filter.getMaxRating()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
