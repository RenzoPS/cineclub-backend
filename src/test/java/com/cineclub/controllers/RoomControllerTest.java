package com.cineclub.controllers;

import com.cineclub.dtos.RoomDto;
import com.cineclub.dtos.SeatDto;
import com.cineclub.entities.Room;
import com.cineclub.entities.Seat;
import com.cineclub.mappers.RoomMapper;
import com.cineclub.mappers.SeatMapper;
import com.cineclub.services.RoomService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cineclub.security.SecurityConfig;
import com.cineclub.security.CustomUserDetailsService;

@WebMvcTest(controllers = RoomController.class)
@Import(SecurityConfig.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomMapper roomMapper;

    @MockBean
    private SeatService seatService;

    @MockBean
    private SeatMapper seatMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private RoomDto roomDto;
    private Room room;
    private Seat seat;
    private SeatDto seatDto;

    @BeforeEach
    void setUp() {
        roomDto = new RoomDto();
        roomDto.setNumber(1);
        roomDto.setCapacity(50);

        room = new Room();
        room.setId(1L);
        room.setNumber(1);
        room.setCapacity(50);

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
        when(roomMapper.toEntity(any(RoomDto.class))).thenReturn(room);
        when(roomService.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.capacity").value(50));
    }

    @Test
    void testCreate_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreate_Forbidden_AsUser() throws Exception {
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAll_Success_AsAdmin() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Room> roomPage = new PageImpl<>(List.of(room), pageable, 1);
        when(roomService.findAll(any())).thenReturn(roomPage);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].number").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAll_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAll_Forbidden_AsUser() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetById_Success_AsAdmin() throws Exception {
        // Arrange
        roomDto.setId(1L);
        when(roomService.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void testGetById_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetById_Forbidden_AsUser() throws Exception {
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSeatsByRoomId_Success_AsAdmin() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Seat> seatPage = new PageImpl<>(List.of(seat), pageable, 1);
        when(seatService.findByRoomId(eq(1L), any(org.springframework.data.domain.Pageable.class))).thenReturn(seatPage);
        when(seatMapper.toDto(any(Seat.class))).thenReturn(seatDto);

        // Act & Assert
        mockMvc.perform(get("/api/rooms/1/seats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].rowLetter").value("A"))
                .andExpect(jsonPath("$.content[0].seatNumber").value(1));
    }

    @Test
    void testGetSeatsByRoomId_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms/1/seats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetSeatsByRoomId_Forbidden_AsUser() throws Exception {
        mockMvc.perform(get("/api/rooms/1/seats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetByRoomAndRow_Success_AsAdmin() throws Exception {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Seat> seatPage = new PageImpl<>(List.of(seat), pageable, 1);
        when(seatService.findByRoomIdAndRowLetter(eq(1L), eq("A"), any(org.springframework.data.domain.Pageable.class))).thenReturn(seatPage);
        when(seatMapper.toDto(any(Seat.class))).thenReturn(seatDto);

        // Act & Assert
        mockMvc.perform(get("/api/rooms/1/seats/row/A"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].rowLetter").value("A"));
    }

    @Test
    void testGetByRoomAndRow_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms/1/seats/row/A"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetByRoomAndRow_Forbidden_AsUser() throws Exception {
        mockMvc.perform(get("/api/rooms/1/seats/row/A"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetByNumber_Success_AsAdmin() throws Exception {
        // Arrange
        when(roomService.findByNumber(1)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(get("/api/rooms/number/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void testGetByNumber_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms/number/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetByNumber_Forbidden_AsUser() throws Exception {
        mockMvc.perform(get("/api/rooms/number/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdate_Success_AsAdmin() throws Exception {
        // Arrange
        roomDto.setId(1L);
        when(roomMapper.toEntity(any(RoomDto.class))).thenReturn(room);
        when(roomService.update(eq(1L), any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void testUpdate_Unauthorized_WithoutAuth() throws Exception {
        roomDto.setId(1L);
        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdate_Forbidden_AsUser() throws Exception {
        roomDto.setId(1L);
        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPartialUpdate_Success_AsAdmin() throws Exception {
        // Arrange
        roomDto.setId(1L);
        roomDto.setNumber(2);
        roomDto.setCapacity(100);
        
        Room updatedRoom = new Room();
        updatedRoom.setId(1L);
        updatedRoom.setNumber(2);
        updatedRoom.setCapacity(100);
        
        when(roomService.partialUpdate(eq(1L), eq(2), eq(100))).thenReturn(updatedRoom);
        
        RoomDto responseDto = new RoomDto();
        responseDto.setId(1L);
        responseDto.setNumber(2);
        responseDto.setCapacity(100);
        when(roomMapper.toDto(any(Room.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(patch("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(2));
    }

    @Test
    void testPartialUpdate_Unauthorized_WithoutAuth() throws Exception {
        roomDto.setId(1L);
        roomDto.setNumber(2);
        roomDto.setCapacity(100);
        mockMvc.perform(patch("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testPartialUpdate_Forbidden_AsUser() throws Exception {
        roomDto.setId(1L);
        roomDto.setNumber(2);
        roomDto.setCapacity(100);
        mockMvc.perform(patch("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete_Success_AsAdmin() throws Exception {
        // Arrange
        doNothing().when(roomService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());
        
        verify(roomService, times(1)).delete(1L);
    }

    @Test
    void testDelete_Unauthorized_WithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDelete_Forbidden_AsUser() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_InvalidNumber() throws Exception {
        // Arrange
        roomDto.setNumber(0); // Menor al mínimo

        // Act & Assert
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError_InvalidCapacity() throws Exception {
        // Arrange
        roomDto.setCapacity(0); // Menor al mínimo

        // Act & Assert
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isBadRequest());
    }
}


