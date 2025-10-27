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
public class RoomDto {
    private Long id;

    @NotNull(message = "El numero de la sala es obligatorio")
    @Min(value = 1, message = "El numero de la sala debe ser al menos 1")
    @Max(value = 100, message = "El numero de la sala no puede exceder 100")
    private Integer number;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 500, message = "La capacidad no puede exceder 500")
    private Integer capacity;


}
