package com.cineclub.mappers;

import com.cineclub.dtos.RoomDto;
import com.cineclub.entities.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDto toDto(Room room);
    Room toEntity(RoomDto roomDto);
}