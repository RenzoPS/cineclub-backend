package com.cineclub.mappers;

import com.cineclub.dtos.MovieDto;
import com.cineclub.dtos.MovieResponseDto;
import com.cineclub.entities.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieDto toDto(Movie movie);
    
    Movie toEntity(MovieDto movieDto);

    @Mapping(target = "screeningIds", expression = "java(movie.getScreenings().stream().map(s -> s.getId()).toList())")
    MovieResponseDto toResponseDto(Movie movie);
}
