package com.cineclub.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "seats")
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_seat_position",
        columnNames = {"room_id", "row_letter", "seat_number"}
    )
})
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotBlank
    @Size(min = 1, max = 2)
    @Column(name = "row_letter", nullable = false, length = 2)
    private String rowLetter;

    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;
}