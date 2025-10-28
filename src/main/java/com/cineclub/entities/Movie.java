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
    private String title;
    private String description;
    private Integer duration;  // Duracion en minutos
    private String genre;
    private Integer rating;

    @OneToMany(mappedBy = "movie")
    private List<Screening> screenings = new ArrayList<>();

}
