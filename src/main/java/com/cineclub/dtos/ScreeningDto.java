package com.cineclub.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningDto {
    private Long id;

    @NotNull(message = "El ID de la película es obligatorio")
    private Long movieId;

    @NotNull(message = "El ID de la sala es obligatorio")
    private Long roomId;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime startTime;
}
