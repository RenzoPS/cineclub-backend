package com.cineclub.mappers;

import com.cineclub.dtos.ScreeningDto;
import com.cineclub.dtos.ScreeningResponseDto;
import com.cineclub.entities.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MovieMapper.class, RoomMapper.class})
public interface ScreeningMapper {

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "roomId", source = "room.id")
    ScreeningDto toDto(Screening screening);

    Screening toEntity(ScreeningDto screeningDto);

    ScreeningResponseDto toResponseDto(Screening screening);
}
