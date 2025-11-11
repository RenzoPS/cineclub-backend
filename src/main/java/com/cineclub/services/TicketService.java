package com.cineclub.services;

import com.cineclub.entities.*;
import com.cineclub.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    @Transactional
    public Ticket confirmTicket(Long ticketId, Float price, User user) {
        Ticket ticket = findTicket(ticketId);
        validateOwnership(ticket, user);
        
        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new IllegalStateException("El ticket no está reservado");
        }
        
        ticket.setStatus(TicketStatus.PAID);
        ticket.setPrice(price);
        ticket.setPurchaseDate(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public void cancelTicket(Long ticketId, User user) {
        Ticket ticket = findTicket(ticketId);
        validateOwnership(ticket, user);
        
        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new IllegalStateException("Solo se pueden cancelar tickets reservados");
        }
        
        ticket.getSeat().setIsAvailable(true);
        ticketRepository.delete(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
    
    private Ticket findTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }
    
    private void validateOwnership(Ticket ticket, User user) {
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("No tienes permiso");
        }
    }
}
