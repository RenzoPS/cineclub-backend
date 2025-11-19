package com.cineclub.controllers;

import com.cineclub.dtos.UserDto;
import com.cineclub.entities.User;
import com.cineclub.mappers.UserMapper;
import com.cineclub.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class
        })
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Juan Perez");
        userDto.setEmail("juan@example.com");
        userDto.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Juan Perez");
        user.setEmail("juan@example.com");
        user.setPassword("encodedPassword");
        user.setAdmin(false);
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(authService.register(any(User.class))).thenReturn(user);
        
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Juan Perez");
        responseDto.setEmail("juan@example.com");
        when(userMapper.toDto(any(User.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testRegister_ValidationError_EmptyName() throws Exception {
        // Arrange
        userDto.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_ValidationError_InvalidEmail() throws Exception {
        // Arrange
        userDto.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_ValidationError_ShortPassword() throws Exception {
        // Arrange
        userDto.setPassword("12345"); // Menos de 6 caracteres

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }
}

