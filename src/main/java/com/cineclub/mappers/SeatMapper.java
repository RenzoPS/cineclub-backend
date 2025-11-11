package com.cineclub.mappers;

import com.cineclub.dtos.SeatDto;
import com.cineclub.entities.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    
    @Mapping(source = "room.id", target = "roomId")
    SeatDto toDto(Seat seat);
    
    java.util.List<SeatDto> toDtoList(java.util.List<Seat> seats);
    
    Seat toEntity(SeatDto seatDto);

}
