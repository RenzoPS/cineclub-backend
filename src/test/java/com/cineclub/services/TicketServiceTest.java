package com.cineclub.services;

import com.cineclub.entities.*;
import com.cineclub.repositories.SeatRepository;
import com.cineclub.repositories.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private HoldService holdService;

    @InjectMocks private TicketService ticketService;

    private User user;
    private User otherUser;
    private Seat seat;
    private Screening screening;
    private Hold hold;
    private Ticket reservedTicket;

    @BeforeEach
    void setUp() {
        user = new User(); user.setId(1L); user.setEmail("user@example.com");
        otherUser = new User(); otherUser.setId(2L); otherUser.setEmail("other@example.com");

        seat = new Seat(); seat.setId(10L); seat.setIsAvailable(false);
        screening = new Screening(); screening.setId(100L);
        hold = new Hold(); hold.setId(1000L);

        reservedTicket = new Ticket();
        reservedTicket.setId(500L);
        reservedTicket.setUser(user);
        reservedTicket.setSeat(seat);
        reservedTicket.setScreening(screening);
        reservedTicket.setHold(hold);
        reservedTicket.setStatus(TicketStatus.RESERVED);
    }

    @Test
    void testConfirmTicket() {
        // Arrange
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(reservedTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Ticket result = ticketService.confirmTicket(500L, user);

        // Assert
        assertNotNull(result);
        assertEquals(TicketStatus.PAID, result.getStatus());
        assertNotNull(result.getPurchaseDate());
        verify(ticketRepository, times(1)).save(reservedTicket);
        verify(holdService, times(1)).updateHoldStatus(hold);
    }

    @Test
    void testConfirmTicket_NotReserved_throws() {
        // Arrange
        Ticket t = copy(reservedTicket); t.setStatus(TicketStatus.CANCELLED);
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(t));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> ticketService.confirmTicket(500L, user));
    }

    @Test
    void testConfirmTicket_Ownership_throws() {
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(reservedTicket));
        assertThrows(IllegalStateException.class, () -> ticketService.confirmTicket(500L, otherUser));
    }

    @Test
    void testConfirmTicket_NotFound_throws() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> ticketService.confirmTicket(999L, user));
    }

    @Test
    void testCancelTicket() {
        // Arrange
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(reservedTicket));

        // Act
        ticketService.cancelTicket(500L, user);

        // Assert
        assertTrue(seat.getIsAvailable());
        verify(seatRepository, times(1)).save(seat);
        verify(ticketRepository, times(1)).save(reservedTicket);
        assertEquals(TicketStatus.CANCELLED, reservedTicket.getStatus());
        verify(holdService, times(1)).updateHoldStatus(hold);
    }

    @Test
    void testCancelTicket_NotReserved_throws() {
        Ticket t = copy(reservedTicket); t.setStatus(TicketStatus.PAID);
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(t));
        assertThrows(IllegalStateException.class, () -> ticketService.cancelTicket(500L, user));
    }

    @Test
    void testCancelTicket_Ownership_throws() {
        when(ticketRepository.findById(500L)).thenReturn(Optional.of(reservedTicket));
        assertThrows(IllegalStateException.class, () -> ticketService.cancelTicket(500L, otherUser));
    }

    @Test
    void testGetUserTickets() {
        when(ticketRepository.findByUserId(1L)).thenReturn(List.of(reservedTicket));
        List<Ticket> result = ticketService.getUserTickets(1L);
        assertEquals(1, result.size());
        assertEquals(500L, result.get(0).getId());
        verify(ticketRepository, times(1)).findByUserId(1L);
    }

    private static Ticket copy(Ticket src) {
        Ticket t = new Ticket();
        t.setId(src.getId());
        t.setUser(src.getUser());
        t.setSeat(src.getSeat());
        t.setScreening(src.getScreening());
        t.setHold(src.getHold());
        t.setStatus(src.getStatus());
        t.setPurchaseDate(src.getPurchaseDate());
        return t;
    }
}
