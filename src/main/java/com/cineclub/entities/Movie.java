package com.cineclub.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(unique = true)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    @Min(value = 1)
    @Max(value = 600)
    private Integer duration;  // Duracion en minutos
    @NotBlank
    @Size(max = 100)
    private String genre;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @OneToMany(mappedBy = "movie")
    private List<Screening> screenings = new ArrayList<>();

}
