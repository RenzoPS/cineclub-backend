package com.cineclub.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScreeningFilterDto {
    private Long movieId;
    private Long roomId;
    private String movieName;
    private String roomNumber;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean today;
}
