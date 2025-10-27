package com.cineclub.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Screening con objetos completos de Movie y Room.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResponseDto {
    private Long id;
    private MovieDto movie;      // Objeto completo de película
    private RoomDto room;        // Objeto completo de sala
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
