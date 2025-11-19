package com.cineclub.services;

import com.cineclub.dtos.HoldDto;
import com.cineclub.entities.*;
import com.cineclub.repositories.*;
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
class HoldServiceTest {

    @Mock private HoldRepository holdRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private UserRepository userRepository;
    @Mock private ScreeningRepository screeningRepository;
    @Mock private SeatRepository seatRepository;

    @InjectMocks private HoldService holdService;

    private User user;
    private Room room;
    private Screening screening;
    private Seat seatA1;
    private Seat seatA2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User");

        room = new Room();
        room.setId(10L);
        room.setNumber(1);

        screening = new Screening();
        screening.setId(100L);
        screening.setRoom(room);
        screening.setStartTime(LocalDateTime.now());
        screening.setEndTime(LocalDateTime.now().plusHours(2));

        seatA1 = new Seat();
        seatA1.setId(1000L);
        seatA1.setRoom(room);
        seatA1.setRowLetter("A");
        seatA1.setSeatNumber(1);
        seatA1.setIsAvailable(true);

        seatA2 = new Seat();
        seatA2.setId(1001L);
        seatA2.setRoom(room);
        seatA2.setRowLetter("A");
        seatA2.setSeatNumber(2);
        seatA2.setIsAvailable(true);
    }

    @Test
    void testCreateHold() {
        // Arrange
        when(ticketRepository.countByUserIdAndScreeningIdAndStatusIn(eq(1L), eq(100L), anyList())).thenReturn(0);
        when(ticketRepository.existsByScreeningIdAndSeatIdAndStatusIn(eq(100L), anyLong(), anyList())).thenReturn(false);
        when(holdRepository.save(any(Hold.class))).thenAnswer(inv -> {
            Hold h = inv.getArgument(0);
            h.setId(500L);
            return h;
        });
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId((long)(Math.random()*10000));
            return t;
        });

        // Act
        List<Ticket> tickets = holdService.createHold(user, screening, List.of(seatA1, seatA2));

        // Assert
        assertNotNull(tickets);
        assertEquals(2, tickets.size());
        verify(holdRepository, times(1)).save(any(Hold.class));
        verify(ticketRepository, times(2)).save(any(Ticket.class));
        verify(seatRepository, times(2)).save(any(Seat.class));
    }

    @Test
    void testCreateHold_WithIds() {
        // Arrange
        HoldDto.SeatSelectionDto sel1 = new HoldDto.SeatSelectionDto("A", 1);
        HoldDto.SeatSelectionDto sel2 = new HoldDto.SeatSelectionDto("A", 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(100L)).thenReturn(Optional.of(screening));
        when(seatRepository.findByRoomIdAndRowLetterAndSeatNumber(10L, "A", 1)).thenReturn(Optional.of(seatA1));
        when(seatRepository.findByRoomIdAndRowLetterAndSeatNumber(10L, "A", 2)).thenReturn(Optional.of(seatA2));

        when(ticketRepository.countByUserIdAndScreeningIdAndStatusIn(eq(1L), eq(100L), anyList())).thenReturn(0);
        when(ticketRepository.existsByScreeningIdAndSeatIdAndStatusIn(eq(100L), anyLong(), anyList())).thenReturn(false);
        when(holdRepository.save(any(Hold.class))).thenAnswer(inv -> inv.getArgument(0));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        List<Ticket> result = holdService.createHold(1L, 100L, List.of(sel1, sel2));

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(screeningRepository, times(1)).findById(100L);
        verify(seatRepository, times(2)).findByRoomIdAndRowLetterAndSeatNumber(eq(10L), eq("A"), anyInt());
    }

    @Test
    void testCreateHold_ExceedsLimit() {
        // Arrange: already has 4
        when(ticketRepository.countByUserIdAndScreeningIdAndStatusIn(eq(1L), eq(100L), anyList())).thenReturn(4);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> holdService.createHold(user, screening, List.of(seatA1)));
    }

    @Test
    void testCreateHold_SeatDifferentRoom() {
        // Arrange
        Room other = new Room(); other.setId(99L);
        Seat badSeat = new Seat(); badSeat.setId(2000L); badSeat.setRoom(other); badSeat.setRowLetter("B"); badSeat.setSeatNumber(1);
        when(ticketRepository.countByUserIdAndScreeningIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> holdService.createHold(user, screening, List.of(badSeat)));
    }

    @Test
    void testCreateHold_SeatAlreadyOccupied() {
        // Arrange
        when(ticketRepository.countByUserIdAndScreeningIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(0);
        when(ticketRepository.existsByScreeningIdAndSeatIdAndStatusIn(eq(100L), eq(1000L), anyList())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> holdService.createHold(user, screening, List.of(seatA1)));
    }

    @Test
    void testExpireOldHolds() {
        // Arrange
        Hold hold = new Hold();
        hold.setId(700L);
        hold.setStatus(HoldStatus.ACTIVE);
        hold.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        hold.setScreening(screening);
        hold.setSeats(List.of(seatA1, seatA2));

        when(holdRepository.findByStatus(HoldStatus.ACTIVE)).thenReturn(List.of(hold));
        when(ticketRepository.findByScreeningIdAndSeatIdInAndStatus(eq(100L), anyList(), eq(TicketStatus.RESERVED)))
                .thenReturn(List.of(new Ticket(), new Ticket()));

        // Act
        int expired = holdService.expireOldHolds();

        // Assert
        assertEquals(1, expired);
        verify(seatRepository, times(2)).save(any(Seat.class));
        verify(ticketRepository, times(1)).deleteAll(anyList());
        verify(holdRepository, times(1)).save(any(Hold.class));
    }

    @Test
    void testUpdateHoldStatus() {
        Hold hold = new Hold();
        hold.setId(800L);
        hold.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Case 1: still reserved -> ACTIVE
        when(ticketRepository.existsByHoldIdAndStatus(800L, TicketStatus.RESERVED)).thenReturn(true);
        holdService.updateHoldStatus(hold);
        assertEquals(HoldStatus.ACTIVE, hold.getStatus());

        // Case 2: still reserved but expired -> EXPIRED
        hold.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(ticketRepository.existsByHoldIdAndStatus(800L, TicketStatus.RESERVED)).thenReturn(true);
        holdService.updateHoldStatus(hold);
        assertEquals(HoldStatus.EXPIRED, hold.getStatus());

        // Case 3: no reserved -> CLOSED
        when(ticketRepository.existsByHoldIdAndStatus(800L, TicketStatus.RESERVED)).thenReturn(false);
        holdService.updateHoldStatus(hold);
        assertEquals(HoldStatus.CLOSED, hold.getStatus());

        verify(holdRepository, times(3)).save(any(Hold.class));
    }
}
