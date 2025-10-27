package com.cineclub.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDto {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private Integer rating;
    private List<Long> screeningIds;
}
