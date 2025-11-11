package com.cineclub.dtos;

import com.cineclub.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private Long userId;
    private Long screeningId;
    private SeatDto seat;
    private Float price;
    private LocalDateTime purchaseDate;
    private TicketStatus status;
    private LocalDateTime expiresAt;
}
