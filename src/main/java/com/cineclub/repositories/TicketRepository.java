package com.cineclub.repositories;

import com.cineclub.entities.Ticket;
import com.cineclub.entities.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Contar tickets de un usuario en una función (RESERVED o PAID)
    int countByUserIdAndScreeningIdAndStatusIn(Long userId, Long screeningId, List<TicketStatus> statuses);
    
    // Verificar si existe un ticket para un asiento (RESERVED o PAID)
    boolean existsByScreeningIdAndSeatIdAndStatusIn(Long screeningId, Long seatId, List<TicketStatus> statuses);
    
    // Buscar tickets de un usuario
    List<Ticket> findByUserId(Long userId);
    
    // Buscar tickets RESERVED de un screening y asientos específicos
    List<Ticket> findByScreeningIdAndSeatIdInAndStatus(Long screeningId, List<Long> seatIds, TicketStatus status);

    // Consultar existencia por hold y estado
    boolean existsByHoldIdAndStatus(Long holdId, TicketStatus status);
}
