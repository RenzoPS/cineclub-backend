package com.cineclub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "holds")
public class Hold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToMany
    @JoinTable(
        name = "hold_seats",
        joinColumns = @JoinColumn(name = "hold_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<Seat> seats;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private HoldStatus status;
}