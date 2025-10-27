package com.cineclub.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieFilterDto {
    private String title;
    private String genre;
    private Integer rating;      // Rating exacto
    private Integer minRating;   // Rating mínimo (rango)
    private Integer maxRating;   // Rating máximo (rango)
}
