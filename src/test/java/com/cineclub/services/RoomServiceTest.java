package com.cineclub.services;

import com.cineclub.entities.Room;
import com.cineclub.entities.Seat;
import com.cineclub.repositories.RoomRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SeatService seatService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RoomService roomService;

    private Room testRoom;

    @BeforeEach
    void setUp(){
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber(1);
        testRoom.setCapacity(10);
    }

    @Test
    void testSave(){
        // Arrange
        when(seatService.generateSeatsForRoom(any(Room.class))).thenReturn(Collections.emptyList());
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // Act
        Room result = roomService.save(testRoom);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getNumber());
        assertEquals(10, result.getCapacity());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testFindAll(){
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Room> roomPage = Page.empty();
        when(roomRepository.findAll(pageable)).thenReturn(roomPage);

        // Act
        Page<Room> result = roomService.findAll(pageable);

        // Assert
        assertNotNull(result);
        verify(roomRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById(){
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        // Act
        Optional<Room> result = roomService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1, result.get().getNumber());
        assertEquals(10, result.get().getCapacity());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByNumber(){
        // Arrange
        when(roomRepository.findByNumber(1)).thenReturn(Optional.of(testRoom));

        // Act
        Optional<Room> result = roomService.findByNumber(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1, result.get().getNumber());
        assertEquals(10, result.get().getCapacity());
        verify(roomRepository, times(1)).findByNumber(1);
    }

    @Test
    void testUpdate(){
        // Arrange
        Room updates = new Room();
        updates.setNumber(2);
        updates.setCapacity(20);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(seatService.generateSeatsForRoom(any(Room.class))).thenReturn(Collections.emptyList());
        
        // Act
        Room result = roomService.update(1L, updates);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getNumber());
        assertEquals(20, result.getCapacity());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void testPartialUpdate(){
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        
        // Act
        Room result = roomService.partialUpdate(1L, 3, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.getNumber());
        assertEquals(10, result.getCapacity());
        verify(roomRepository, times(1)).findById(1L);
        verify(seatService, never()).generateSeatsForRoom(any(Room.class));
    }

    @Test
    void testDelete(){
        // Act
        roomService.delete(1L);
        
        // Assert
        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void saveGeneratesSeats(){
        // Arrange
        when(seatService.generateSeatsForRoom(any(Room.class)))
            .thenReturn(List.of(new Seat()));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // Act
        roomService.save(testRoom);

        // Assert
        verify(seatService, times(1)).generateSeatsForRoom(testRoom);
    }

    @Test
    void updateCapacityRegeneratesSeats(){
        // Arrange
        Room updates = new Room();
        updates.setNumber(1);
        updates.setCapacity(20);
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(seatService.generateSeatsForRoom(any(Room.class)))
            .thenReturn(List.of(new Seat()));

        // Act
        roomService.update(1L, updates);

        // Assert
        verify(seatService, times(1)).generateSeatsForRoom(testRoom);
    }

    @Test
    void updateSameCapacityDoesNotRegenerateSeats(){
        // Arrange
        Room updates = new Room();
        updates.setNumber(2);
        updates.setCapacity(10);
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        // Act
        roomService.update(1L, updates);

        // Assert
        verify(seatService, never()).generateSeatsForRoom(any(Room.class));
    }
}
