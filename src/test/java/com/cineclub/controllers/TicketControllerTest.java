package com.cineclub.controllers;

import com.cineclub.dtos.SeatDto;
import com.cineclub.dtos.TicketDto;
import com.cineclub.entities.*;
import com.cineclub.mappers.TicketMapper;
import com.cineclub.services.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cineclub.security.SecurityConfig;
import com.cineclub.security.CustomUserDetailsService;

@WebMvcTest(controllers = TicketController.class)
@Import(SecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private TicketMapper ticketMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Ticket ticket;
    private TicketDto ticketDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("Test User");

        Screening screening = new Screening();
        screening.setId(1L);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setRowLetter("A");
        seat.setSeatNumber(1);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUser(user);
        ticket.setScreening(screening);
        ticket.setSeat(seat);
        ticket.setPrice(15.0f);
        ticket.setStatus(TicketStatus.RESERVED);

        SeatDto seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDto.setRowLetter("A");
        seatDto.setSeatNumber(1);

        ticketDto = new TicketDto();
        ticketDto.setId(1L);
        ticketDto.setUserId(1L);
        ticketDto.setScreeningId(1L);
        ticketDto.setSeat(seatDto);
        ticketDto.setPrice(15.0f);
        ticketDto.setStatus(TicketStatus.RESERVED);
    }

    @Test
    void testConfirmTicket_Success() throws Exception {
        // Arrange
        Ticket confirmedTicket = new Ticket();
        confirmedTicket.setId(1L);
        confirmedTicket.setStatus(TicketStatus.PAID);
        confirmedTicket.setPurchaseDate(LocalDateTime.now());

        TicketDto confirmedDto = new TicketDto();
        confirmedDto.setId(1L);
        confirmedDto.setStatus(TicketStatus.PAID);

        when(ticketService.confirmTicket(eq(1L), any(User.class))).thenReturn(confirmedTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(confirmedDto);

        // Act & Assert
        mockMvc.perform(post("/api/tickets/1/confirm").with(
                authentication(new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
        ))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testConfirmTicket_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(post("/api/tickets/1/confirm"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCancelTicket_Success() throws Exception {
        // Arrange
        doNothing().when(ticketService).cancelTicket(eq(1L), any(User.class));

        // Act & Assert
        mockMvc.perform(delete("/api/tickets/1").with(
                authentication(new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
        ))
                .andExpect(status().isNoContent());
        
        verify(ticketService, times(1)).cancelTicket(eq(1L), any(User.class));
    }

    @Test
    void testCancelTicket_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMyTickets_Success() throws Exception {
        // Arrange
        when(ticketService.getUserTickets(any(Long.class))).thenReturn(List.of(ticket));
        when(ticketMapper.toDtoList(any(List.class))).thenReturn(List.of(ticketDto));

        // Act & Assert
        mockMvc.perform(get("/api/me/tickets").with(
                authentication(new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
        ))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("RESERVED"));
    }

    @Test
    void testGetMyTickets_EmptyList() throws Exception {
        // Arrange
        when(ticketService.getUserTickets(any(Long.class))).thenReturn(List.of());
        when(ticketMapper.toDtoList(any(List.class))).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/me/tickets").with(
                authentication(new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
        ))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetMyTickets_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/me/tickets"))
                .andExpect(status().isUnauthorized());
    }
}


