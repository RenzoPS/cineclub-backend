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

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "row_letter", nullable = false, length = 2)
    private String rowLetter;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;
}