package com.cineclub.mappers;

import com.cineclub.dtos.TicketDto;
import com.cineclub.entities.Ticket;
import com.cineclub.entities.TicketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SeatMapper.class})
public interface TicketMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "screening.id", target = "screeningId")
    @Mapping(source = "ticket", target = "expiresAt", qualifiedByName = "getExpiresAt")
    TicketDto toDto(Ticket ticket);
    
    List<TicketDto> toDtoList(List<Ticket> tickets);
    
    @Named("getExpiresAt")
    default LocalDateTime getExpiresAt(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.PAID) {
            return null;
        }
        return ticket.getHold() != null ? ticket.getHold().getExpiresAt() : null;
    }
}
