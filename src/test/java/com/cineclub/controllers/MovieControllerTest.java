package com.cineclub.controllers;

import com.cineclub.dtos.MovieDto;
import com.cineclub.dtos.MovieResponseDto;
import com.cineclub.dtos.MovieFilterDto;
import com.cineclub.entities.Movie;
import com.cineclub.mappers.MovieMapper;
import com.cineclub.security.CustomUserDetailsService;
import com.cineclub.security.SecurityConfig;
import com.cineclub.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class)
@Import(SecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private MovieMapper movieMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private MovieDto movieDto;
    private Movie movie;
    private MovieResponseDto movieResponseDto;

    @BeforeEach
    void setUp() {
        movieDto = new MovieDto();
        movieDto.setTitle("Matrix");
        movieDto.setDescription("A movie about a computer hacker");
        movieDto.setDuration(148);
        movieDto.setGenre("Sci-Fi");
        movieDto.setRating(5);

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Matrix");
        movie.setDescription("A movie about a computer hacker");
        movie.setDuration(148);
        movie.setGenre("Sci-Fi");
        movie.setRating(5);

        movieResponseDto = new MovieResponseDto();
        movieResponseDto.setId(1L);
        movieResponseDto.setTitle("Matrix");
        movieResponseDto.setDescription("A movie about a computer hacker");
        movieResponseDto.setDuration(148);
        movieResponseDto.setGenre("Sci-Fi");
        movieResponseDto.setRating(5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_Success_AsAdmin() throws Exception {
        // Arrange
        when(movieMapper.toEntity(any(MovieDto.class))).thenReturn(movie);
        when(movieService.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(any(Movie.class))).thenReturn(movieDto);

        // Act & Assert
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Matrix"))
                .andExpect(jsonPath("$.duration").value(148))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"));
    }

    @Test
    void testCreate_Unauthorized_WithoutAuth() throws Exception {
        // Act & Assert - Sin autenticación debe retornar 401
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreate_Forbidden_AsUser() throws Exception {
        // Act & Assert - USER no tiene permisos para crear, debe retornar 403
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAll_Success_AsUser() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);
        when(movieService.findAll(any())).thenReturn(moviePage);
        when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(movieResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Matrix"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAll_Success_AsAdmin() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);
        when(movieService.findAll(any())).thenReturn(moviePage);
        when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(movieResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Matrix"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAll_Unauthorized_WithoutAuth() throws Exception {
        // Act & Assert - Sin autenticación debe retornar 401
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetById_Success_AsUser() throws Exception {
        // Arrange
        when(movieService.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(movieResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Matrix"));
    }

    @Test
    void testGetById_Unauthorized_WithoutAuth() throws Exception {
        // Act & Assert - Sin autenticación debe retornar 401
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdate_Success_AsAdmin() throws Exception {
        // Arrange
        movieDto.setId(1L);
        when(movieMapper.toEntity(any(MovieDto.class))).thenReturn(movie);
        when(movieService.update(eq(1L), any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(any(Movie.class))).thenReturn(movieDto);

        // Act & Assert
        mockMvc.perform(put("/api/movies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Matrix"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdate_Forbidden_AsUser() throws Exception {
        // Act & Assert - USER no tiene permisos para actualizar, debe retornar 403
        movieDto.setId(1L);
        mockMvc.perform(put("/api/movies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete_Success_AsAdmin() throws Exception {
        // Arrange
        doNothing().when(movieService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());
        
        verify(movieService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDelete_Forbidden_AsUser() throws Exception {
        // Act & Assert - USER no tiene permisos para eliminar, debe retornar 403
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_EmptyTitle() throws Exception {
        // Arrange
        movieDto.setTitle("");

        // Act & Assert
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_InvalidDuration() throws Exception {
        // Arrange
        movieDto.setDuration(0); // Menor al mínimo

        // Act & Assert
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearch_Success_AsUser() throws Exception {
        // Arrange
        MovieFilterDto filter = new MovieFilterDto();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);
        when(movieService.findAllWithSpecification(any(), any())).thenReturn(moviePage);
        when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(movieResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/movies/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Matrix"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testSearch_Unauthorized_WithoutAuth() throws Exception {
        MovieFilterDto filter = new MovieFilterDto();
        mockMvc.perform(post("/api/movies/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearch_Success_AsAdmin() throws Exception {
        // Arrange
        MovieFilterDto filter = new MovieFilterDto();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);
        when(movieService.findAllWithSpecification(any(), any())).thenReturn(moviePage);
        when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(movieResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/movies/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Matrix"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
