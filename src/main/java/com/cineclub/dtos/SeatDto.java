package com.cineclub.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;

    @NotNull(message = "El ID de la sala es obligatorio")
    private Long roomId;

    @NotBlank(message = "La letra de fila es obligatoria")
    @Size(min = 1, max = 2, message = "La letra de fila debe tener entre 1 y 2 caracteres")
    private String rowLetter;

    @NotNull(message = "El número de asiento es obligatorio")
    @Min(value = 1, message = "El número de asiento debe ser al menos 1")
    @Max(value = 50, message = "El número de asiento no puede exceder 50")
    private Integer seatNumber;
}
