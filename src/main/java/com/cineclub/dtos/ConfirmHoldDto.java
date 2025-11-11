package com.cineclub.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmHoldDto {
    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser positivo")
    private Float pricePerTicket;
}
