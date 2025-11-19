package com.cineclub.services;

import com.cineclub.dtos.HoldDto;
import com.cineclub.entities.*;
import com.cineclub.repositories.HoldRepository;
import com.cineclub.repositories.SeatRepository;
import com.cineclub.repositories.ScreeningRepository;
import com.cineclub.repositories.TicketRepository;
import com.cineclub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoldService {
    private final HoldRepository holdRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    
    @Value("${hold.ttl.minutes:10}")
    private int holdTtlMinutes;

    @Transactional
    public List<Ticket> createHold(User user, Screening screening, List<Seat> seats) {
        validateHold(user, screening, seats);
        
        Hold hold = new Hold();
        hold.setUser(user);
        hold.setScreening(screening);
        hold.setSeats(seats);
        hold.setCreatedAt(LocalDateTime.now());
        hold.setExpiresAt(LocalDateTime.now().plusMinutes(holdTtlMinutes));
        hold.setStatus(HoldStatus.ACTIVE);
        hold = holdRepository.save(hold);
        
        return createReservedTickets(user, screening, seats, hold);
    }
    
    private void validateHold(User user, Screening screening, List<Seat> seats) {
        List<TicketStatus> activeStatuses = List.of(TicketStatus.RESERVED, TicketStatus.PAID);
        
        int totalTickets = ticketRepository.countByUserIdAndScreeningIdAndStatusIn(
            user.getId(), screening.getId(), activeStatuses);
        if (totalTickets + seats.size() > 4) {
            throw new IllegalStateException("Máximo 4 asientos por función");
        }
        
        Long roomId = screening.getRoom().getId();
        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(roomId)) {
                throw new IllegalArgumentException("Asiento no pertenece a esta sala");
            }
            if (ticketRepository.existsByScreeningIdAndSeatIdAndStatusIn(
                screening.getId(), seat.getId(), activeStatuses)) {
                throw new IllegalStateException("Asiento ya ocupado");
            }
        }
    }
    
    private List<Ticket> createReservedTickets(User user, Screening screening, List<Seat> seats, Hold hold) {
        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : seats) {
            seat.setIsAvailable(false);
            seatRepository.save(seat);
            
            Ticket ticket = new Ticket();
            ticket.setUser(user);
            ticket.setScreening(screening);
            ticket.setSeat(seat);
            ticket.setHold(hold);
            ticket.setPrice(0.0f);
            ticket.setPurchaseDate(LocalDateTime.now());
            ticket.setStatus(TicketStatus.RESERVED);
            tickets.add(ticketRepository.save(ticket));
        }
        return tickets;
    }

    @Transactional
    public List<Ticket> createHold(Long userId, Long screeningId, List<HoldDto.SeatSelectionDto> seatSelections) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Screening screening = screeningRepository.findById(screeningId)
            .orElseThrow(() -> new IllegalArgumentException("Función no encontrada"));
        
        List<Seat> seats = findSeats(screening.getRoom().getId(), seatSelections);
        return createHold(user, screening, seats);
    }
    
    private List<Seat> findSeats(Long roomId, List<HoldDto.SeatSelectionDto> selections) {
        return selections.stream()
            .map(s -> seatRepository.findByRoomIdAndRowLetterAndSeatNumber(roomId, s.getRowLetter(), s.getSeatNumber())
                .orElseThrow(() -> new IllegalArgumentException("Asiento " + s.getRowLetter() + s.getSeatNumber() + " no encontrado")))
            .toList();
    }

    @Transactional
    public int expireOldHolds() {
        List<Hold> expiredHolds = holdRepository.findByStatus(HoldStatus.ACTIVE).stream()
            .filter(h -> h.getExpiresAt().isBefore(LocalDateTime.now()))
            .toList();
        
        expiredHolds.forEach(this::expireHold);
        return expiredHolds.size();
    }
    
    private void expireHold(Hold hold) {
        List<Long> seatIds = hold.getSeats().stream().map(Seat::getId).toList();
        List<Ticket> tickets = ticketRepository.findByScreeningIdAndSeatIdInAndStatus(
            hold.getScreening().getId(), seatIds, TicketStatus.RESERVED);
        
        hold.getSeats().forEach(seat -> {
            seat.setIsAvailable(true);
            seatRepository.save(seat);
        });
        
        ticketRepository.deleteAll(tickets);
        hold.setStatus(HoldStatus.EXPIRED);
        holdRepository.save(hold);
    }

    public void updateHoldStatus(Hold hold) {
        var now = LocalDateTime.now();
        Long holdId = hold.getId();

        boolean anyReserved = ticketRepository.existsByHoldIdAndStatus(holdId, TicketStatus.RESERVED);

        if (anyReserved) {
            hold.setStatus(now.isBefore(hold.getExpiresAt()) ? HoldStatus.ACTIVE : HoldStatus.EXPIRED);
        } else {
            hold.setStatus(HoldStatus.CLOSED);
        }
        holdRepository.save(hold);
    }
}
