package com.cineclub.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoldDto {
    @NotEmpty(message = "Debe seleccionar al menos un asiento")
    private List<SeatSelectionDto> seats;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatSelectionDto {
        private String rowLetter;
        private Integer seatNumber;
    }
}
