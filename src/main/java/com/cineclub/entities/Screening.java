package com.cineclub.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "screenings")
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_screening_room_time",
        columnNames = {"room_id", "start_time"}
    )
})
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
