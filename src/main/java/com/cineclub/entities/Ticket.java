package com.cineclub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
    
    @ManyToOne
    @JoinColumn(name = "hold_id")
    private Hold hold;

    private Float price;
    private LocalDateTime purchaseDate;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

}

