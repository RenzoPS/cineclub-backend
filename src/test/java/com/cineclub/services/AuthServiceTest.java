package com.cineclub.services;

import com.cineclub.entities.User;
import com.cineclub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Juan Perez");
        testUser.setEmail("juan@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testRegister() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authService.register(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@example.com", result.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }
}
