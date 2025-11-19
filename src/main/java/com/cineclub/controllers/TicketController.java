package com.cineclub.controllers;

import com.cineclub.dtos.TicketDto;
import com.cineclub.entities.Ticket;
import com.cineclub.entities.User;
import com.cineclub.mappers.TicketMapper;
import com.cineclub.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {
    
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PostMapping("/tickets/{ticketId}/confirm")
    public ResponseEntity<TicketDto> confirmTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal User user) {
        
        Ticket ticket = ticketService.confirmTicket(ticketId, user);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal User user) {
        
        ticketService.cancelTicket(ticketId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/tickets")
    public ResponseEntity<List<TicketDto>> getMyTickets(@AuthenticationPrincipal User user) {
        List<Ticket> tickets = ticketService.getUserTickets(user.getId());
        return ResponseEntity.ok(ticketMapper.toDtoList(tickets));
    }
}
