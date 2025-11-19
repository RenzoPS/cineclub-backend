package com.cineclub.controllers;

import com.cineclub.dtos.MovieDto;
import com.cineclub.dtos.RoomDto;
import com.cineclub.dtos.ScreeningDto;
import com.cineclub.dtos.ScreeningResponseDto;
import com.cineclub.dtos.SeatDto;
import com.cineclub.dtos.ScreeningFilterDto;
import com.cineclub.dtos.HoldDto;
import com.cineclub.entities.Movie;
import com.cineclub.entities.Room;
import com.cineclub.entities.Screening;
import com.cineclub.entities.Seat;
import com.cineclub.entities.Ticket;
import com.cineclub.entities.User;
import com.cineclub.mappers.ScreeningMapper;
import com.cineclub.mappers.SeatMapper;
import com.cineclub.mappers.TicketMapper;
import com.cineclub.services.HoldService;
import com.cineclub.services.MovieService;
import com.cineclub.services.RoomService;
import com.cineclub.services.ScreeningService;
import com.cineclub.services.SeatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cineclub.security.SecurityConfig;
import com.cineclub.security.CustomUserDetailsService;

@WebMvcTest(controllers = ScreeningController.class)
@Import(SecurityConfig.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScreeningService screeningService;

    @MockBean
    private ScreeningMapper screeningMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private SeatService seatService;

    @MockBean
    private SeatMapper seatMapper;

    @MockBean
    private HoldService holdService;

    @MockBean
    private TicketMapper ticketMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private ScreeningDto screeningDto;
    private Screening screening;
    private ScreeningResponseDto screeningResponseDto;
    private Movie movie;
    private Room room;
    private Seat seat;
    private SeatDto seatDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Matrix");
        movie.setDuration(148);

        room = new Room();
        room.setId(1L);
        room.setNumber(1);
        room.setCapacity(50);

        screeningDto = new ScreeningDto();
        screeningDto.setMovieId(1L);
        screeningDto.setRoomId(1L);
        screeningDto.setStartTime(LocalDateTime.of(2024, 12, 25, 20, 0));

        screening = new Screening();
        screening.setId(1L);
        screening.setMovie(movie);
        screening.setRoom(room);
        screening.setStartTime(LocalDateTime.of(2024, 12, 25, 20, 0));
        screening.setEndTime(LocalDateTime.of(2024, 12, 25, 22, 28));

        screeningResponseDto = new ScreeningResponseDto();
        screeningResponseDto.setId(1L);
        screeningResponseDto.setStartTime(LocalDateTime.of(2024, 12, 25, 20, 0));
        screeningResponseDto.setEndTime(LocalDateTime.of(2024, 12, 25, 22, 28));

        seat = new Seat();
        seat.setId(1L);
        seat.setRoom(room);
        seat.setRowLetter("A");
        seat.setSeatNumber(1);
        seat.setIsAvailable(true);

        seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDto.setRoomId(1L);
        seatDto.setRowLetter("A");
        seatDto.setSeatNumber(1);
        seatDto.setIsAvailable(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_Success_AsAdmin() throws Exception {
        // Arrange
        when(screeningMapper.toEntity(any(ScreeningDto.class))).thenReturn(screening);
        when(movieService.findById(1L)).thenReturn(Optional.of(movie));
        when(roomService.findById(1L)).thenReturn(Optional.of(room));
        when(screeningService.save(any(Screening.class))).thenReturn(screening);
        when(screeningMapper.toDto(any(Screening.class))).thenReturn(screeningDto);

        // Act & Assert
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId").value(1L))
                .andExpect(jsonPath("$.roomId").value(1L));
    }

    @Test
    void testCreate_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreate_Forbidden_AsUser() throws Exception {
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAll_Success_AsUser() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Screening> screeningPage = new PageImpl<>(List.of(screening), pageable, 1);
        when(screeningService.findAll(any())).thenReturn(screeningPage);
        when(screeningMapper.toResponseDto(any(Screening.class))).thenReturn(screeningResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAll_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetById_Success_AsUser() throws Exception {
        // Arrange
        when(screeningService.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningMapper.toResponseDto(any(Screening.class))).thenReturn(screeningResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/screenings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetById_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/screenings/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetSeats_Success_AsUser() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Seat> seatPage = new PageImpl<>(List.of(seat), pageable, 1);
        when(screeningService.findById(1L)).thenReturn(Optional.of(screening));
        when(seatService.getSeatsForScreening(any(Screening.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(seatPage);
        when(seatMapper.toDto(any(Seat.class))).thenReturn(seatDto);

        // Act & Assert
        mockMvc.perform(get("/api/screenings/1/seats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].rowLetter").value("A"))
                .andExpect(jsonPath("$.content[0].seatNumber").value(1));
    }

    @Test
    void testGetSeats_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/screenings/1/seats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdate_Success_AsAdmin() throws Exception {
        // Arrange
        screeningDto.setId(1L);
        when(screeningService.update(eq(1L), eq(1L), eq(1L), any(LocalDateTime.class))).thenReturn(screening);
        when(screeningMapper.toDto(any(Screening.class))).thenReturn(screeningDto);

        // Act & Assert
        mockMvc.perform(put("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId").value(1L));
    }

    @Test
    void testUpdate_Unauthorized_WithoutAuth() throws Exception {
        screeningDto.setId(1L);
        mockMvc.perform(put("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdate_Forbidden_AsUser() throws Exception {
        screeningDto.setId(1L);
        mockMvc.perform(put("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPartialUpdate_Success_AsAdmin() throws Exception {
        // Arrange
        screeningDto.setId(1L);
        when(screeningService.partialUpdate(eq(1L), eq(1L), eq(1L), any(LocalDateTime.class))).thenReturn(screening);
        when(screeningMapper.toDto(any(Screening.class))).thenReturn(screeningDto);

        // Act & Assert
        mockMvc.perform(patch("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId").value(1L));
    }

    @Test
    void testPartialUpdate_Unauthorized_WithoutAuth() throws Exception {
        screeningDto.setId(1L);
        mockMvc.perform(patch("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testPartialUpdate_Forbidden_AsUser() throws Exception {
        screeningDto.setId(1L);
        mockMvc.perform(patch("/api/screenings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete_Success_AsAdmin() throws Exception {
        // Arrange
        doNothing().when(screeningService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/screenings/1"))
                .andExpect(status().isNoContent());

        verify(screeningService, times(1)).delete(1L);
    }

    @Test
    void testDelete_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/screenings/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDelete_Forbidden_AsUser() throws Exception {
        mockMvc.perform(delete("/api/screenings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_MissingMovieId() throws Exception {
        // Arrange
        screeningDto.setMovieId(null);

        // Act & Assert
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_MissingStartTime() throws Exception {
        // Arrange
        screeningDto.setStartTime(null);

        // Act & Assert
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screeningDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearch_Success_AsUser() throws Exception {
        // Arrange
        ScreeningFilterDto filter = new ScreeningFilterDto();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Screening> screeningPage = new PageImpl<>(List.of(screening), pageable, 1);
        when(screeningService.findWithSpecification(any(), any())).thenReturn(screeningPage);
        when(screeningMapper.toResponseDto(any(Screening.class))).thenReturn(screeningResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/screenings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testSearch_Unauthorized_WithoutAuth() throws Exception {
        ScreeningFilterDto filter = new ScreeningFilterDto();
        mockMvc.perform(post("/api/screenings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateHold_Success_AsUser() throws Exception {
        // Arrange
        HoldDto.SeatSelectionDto sel = new HoldDto.SeatSelectionDto("A", 1);
        HoldDto hold = new HoldDto(List.of(sel));
        when(screeningService.findById(1L)).thenReturn(Optional.of(screening));
        List<Ticket> tickets = List.of(new Ticket());
        when(holdService.createHold(eq(1L), eq(1L), any(List.class))).thenReturn(tickets);
        when(ticketMapper.toDtoList(any(List.class))).thenReturn(List.of(new com.cineclub.dtos.TicketDto()));

        // Act & Assert
        mockMvc.perform(post("/api/screenings/1/holds")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hold)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateHold_Unauthorized_WithoutAuth() throws Exception {
        HoldDto hold = new HoldDto(List.of(new HoldDto.SeatSelectionDto("A", 1)));
        mockMvc.perform(post("/api/screenings/1/holds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hold)))
                .andExpect(status().isUnauthorized());
    }

}
