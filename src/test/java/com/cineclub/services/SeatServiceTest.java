package com.cineclub.services;

import com.cineclub.entities.Room;
import com.cineclub.entities.Seat;
import com.cineclub.repositories.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    private Room testRoom;
    private Seat testSeat;

    @BeforeEach
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber(1);
        testRoom.setCapacity(50);

        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setRoom(testRoom);
        testSeat.setRowLetter("A");
        testSeat.setSeatNumber(1);
    }

    @Test
    void testFindByRoomId() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Seat> seatPage = Page.empty();
        when(seatRepository.findByRoomId(1L, pageable)).thenReturn(seatPage);

        // Act
        Page<Seat> result = seatService.findByRoomId(1L, pageable);

        // Assert
        assertNotNull(result);
        verify(seatRepository, times(1)).findByRoomId(1L, pageable);
    }

    @Test
    void testFindByRoomIdAndRowLetter() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Seat> seatPage = Page.empty();
        when(seatRepository.findByRoomIdAndRowLetter(1L, "A", pageable)).thenReturn(seatPage);

        // Act
        Page<Seat> result = seatService.findByRoomIdAndRowLetter(1L, "A", pageable);

        // Assert
        assertNotNull(result);
        verify(seatRepository, times(1)).findByRoomIdAndRowLetter(1L, "A", pageable);
    }

    @Test
    void testGenerateSeatsForRoom() {
        // Arrange
        Room room = new Room();
        room.setId(1L);
        room.setCapacity(25);

        // Act
        List<Seat> result = seatService.generateSeatsForRoom(room);

        // Assert
        assertNotNull(result);
        assertEquals(25, result.size());
        
        // Verify first seat (A1)
        Seat firstSeat = result.get(0);
        assertEquals("A", firstSeat.getRowLetter());
        assertEquals(1, firstSeat.getSeatNumber());
        assertEquals(room, firstSeat.getRoom());

        // Verify 10th seat (A10)
        Seat tenthSeat = result.get(9);
        assertEquals("A", tenthSeat.getRowLetter());
        assertEquals(10, tenthSeat.getSeatNumber());

        // Verify 11th seat (B1)
        Seat eleventhSeat = result.get(10);
        assertEquals("B", eleventhSeat.getRowLetter());
        assertEquals(1, eleventhSeat.getSeatNumber());

        // Verify last seat (C5)
        Seat lastSeat = result.get(24);
        assertEquals("C", lastSeat.getRowLetter());
        assertEquals(5, lastSeat.getSeatNumber());
    }
}
