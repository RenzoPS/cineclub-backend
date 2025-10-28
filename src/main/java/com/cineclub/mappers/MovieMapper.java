package com.cineclub.mappers;

import com.cineclub.dtos.MovieDto;
import com.cineclub.dtos.MovieResponseDto;
import com.cineclub.entities.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieDto toDto(Movie movie);
    
    Movie toEntity(MovieDto movieDto);

    @Mapping(target = "screeningIds", source = "movie", qualifiedByName = "mapScreeningIds")
    MovieResponseDto toResponseDto(Movie movie);

    @Named("mapScreeningIds")
    default List<Long> mapScreeningIds(Movie movie) {
        return movie.getScreenings().stream().map(s -> s.getId()).toList();
    }
}
