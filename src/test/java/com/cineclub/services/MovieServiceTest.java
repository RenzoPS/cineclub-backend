package com.cineclub.services;

import com.cineclub.entities.Movie;
import com.cineclub.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("Test Movie Description");
        testMovie.setDuration(120);
        testMovie.setGenre("Test Movie Genre");
        testMovie.setRating(5);
    }

    @Test
    void testSave(){
        // Arrange
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // Act
        Movie result = movieService.save(testMovie);

        // Assert
        assertNotNull(result);
        assertEquals("Test Movie", result.getTitle());
        assertEquals("Test Movie Description", result.getDescription());
        assertEquals(120, result.getDuration());
        assertEquals("Test Movie Genre", result.getGenre());
        assertEquals(5, result.getRating());
        verify(movieRepository, times(1)).save(testMovie);
    }

    @Test
    void testFindAll(){
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Movie> moviePage = Page.empty();
        when(movieRepository.findAll(pageable)).thenReturn(moviePage);
        
        // Act
        Page<Movie> result = movieService.findAll(pageable);
        
        // Assert
        assertNotNull(result);
        verify(movieRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById(){
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        
        // Act
        Optional<Movie> result = movieService.findById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Movie", result.get().getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdate(){
        // Arrange
        Movie updates = new Movie();
        updates.setTitle("Updated Movie Title");
        updates.setDescription("Updated Movie Description");
        updates.setDuration(150);
        updates.setGenre("Updated Movie Genre");
        updates.setRating(4);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        
        // Act
        Movie result = movieService.update(1L, updates);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Movie Title", result.getTitle());
        assertEquals("Updated Movie Description", result.getDescription());
        assertEquals(150, result.getDuration());
        assertEquals(4, result.getRating());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void testPartialUpdate(){
        // Arrange
        Movie updates = new Movie();
        updates.setTitle("Partial Update Movie Title");
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        // Act
        Movie result = movieService.partialUpdate(1L, updates);

        // Assert
        assertNotNull(result);
        assertEquals("Partial Update Movie Title", result.getTitle());
        assertEquals("Test Movie Description", result.getDescription());
        assertEquals(120, result.getDuration());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllWithSpecification(){
        // Arrange
        Specification<Movie> spec = mock(Specification.class);
        Pageable pageable = Pageable.unpaged();
        Page<Movie> moviePage = Page.empty();
        when(movieRepository.findAll(spec, pageable)).thenReturn(moviePage);
        
        // Act
        Page<Movie> result = movieService.findAllWithSpecification(spec, pageable);
        
        // Assert
        assertNotNull(result);
        verify(movieRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testDelete(){
        // Act
        movieService.delete(1L);

        // Assert
        verify(movieRepository, times(1)).deleteById(1L);
    }
}
