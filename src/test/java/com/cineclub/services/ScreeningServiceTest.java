package com.cineclub.services;

import com.cineclub.entities.Movie;
import com.cineclub.entities.Room;
import com.cineclub.entities.Screening;
import com.cineclub.repositories.ScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {
    
    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ScreeningService screeningService;

    private Screening testScreening;
    private Movie testMovie;
    private Room testRoom;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDuration(120);

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber(1);
        testRoom.setCapacity(50);

        testStartTime = LocalDateTime.of(2025, 10, 27, 18, 0);
        testEndTime = testStartTime.plusHours(2);

        testScreening = new Screening();
        testScreening.setId(1L);
        testScreening.setMovie(testMovie);
        testScreening.setRoom(testRoom);
        testScreening.setStartTime(testStartTime);
        testScreening.setEndTime(testEndTime);
    }

    @Test
    void testSave() {
        // Arrange
        when(screeningRepository.save(any(Screening.class))).thenReturn(testScreening);

        // Act
        Screening result = screeningService.save(testScreening);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testMovie, result.getMovie());
        assertEquals(testRoom, result.getRoom());
        assertEquals(testStartTime, result.getStartTime());
        assertEquals(testEndTime, result.getEndTime());
        verify(screeningRepository, times(1)).save(testScreening);
    }

    @Test
    void testFindAll() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Screening> screeningPage = Page.empty();
        when(screeningRepository.findAll(pageable)).thenReturn(screeningPage);

        // Act
        Page<Screening> result = screeningService.findAll(pageable);

        // Assert
        assertNotNull(result);
        verify(screeningRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindWithSpecification() {
        // Arrange
        Specification<Screening> spec = mock(Specification.class);
        Pageable pageable = Pageable.unpaged();
        Page<Screening> screeningPage = Page.empty();
        when(screeningRepository.findAll(spec, pageable)).thenReturn(screeningPage);

        // Act
        Page<Screening> result = screeningService.findWithSpecification(spec, pageable);

        // Assert
        assertNotNull(result);
        verify(screeningRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testFindById() {
        // Arrange
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(testScreening));

        // Act
        Optional<Screening> result = screeningService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(testMovie, result.get().getMovie());
        assertEquals(testRoom, result.get().getRoom());
        verify(screeningRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdate() {
        // Arrange
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L);
        updatedMovie.setTitle("Updated Movie");

        Room updatedRoom = new Room();
        updatedRoom.setId(2L);
        updatedRoom.setNumber(2);

        LocalDateTime updatedStartTime = LocalDateTime.of(2025, 10, 28, 20, 0);
        LocalDateTime updatedEndTime = updatedStartTime.plusHours(2);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(testScreening));
        when(movieService.findById(2L)).thenReturn(Optional.of(updatedMovie));
        when(roomService.findById(2L)).thenReturn(Optional.of(updatedRoom));

        // Act
        Screening result = screeningService.update(1L, 2L, 2L, updatedStartTime, updatedEndTime);

        // Assert
        assertNotNull(result);
        assertEquals(updatedMovie, result.getMovie());
        assertEquals(updatedRoom, result.getRoom());
        assertEquals(updatedStartTime, result.getStartTime());
        assertEquals(updatedEndTime, result.getEndTime());
        verify(screeningRepository, times(1)).findById(1L);
        verify(movieService, times(1)).findById(2L);
        verify(roomService, times(1)).findById(2L);
    }

    @Test
    void testPartialUpdate() {
        // Arrange
        LocalDateTime updatedStartTime = LocalDateTime.of(2025, 10, 28, 20, 0);
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(testScreening));

        // Act
        Screening result = screeningService.partialUpdate(1L, null, null, updatedStartTime, null);

        // Assert
        assertNotNull(result);
        assertEquals(testMovie, result.getMovie());
        assertEquals(testRoom, result.getRoom());
        assertEquals(updatedStartTime, result.getStartTime());
        assertEquals(testEndTime, result.getEndTime());
        verify(screeningRepository, times(1)).findById(1L);
        verify(movieService, never()).findById(any());
        verify(roomService, never()).findById(any());
    }

    @Test
    void testDelete() {
        // Act
        screeningService.delete(1L);

        // Assert
        verify(screeningRepository, times(1)).deleteById(1L);
    }
}
