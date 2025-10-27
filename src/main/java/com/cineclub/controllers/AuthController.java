package com.cineclub.controllers;

import com.cineclub.dtos.UserDto;
import com.cineclub.entities.User;
import com.cineclub.mappers.UserMapper;
import com.cineclub.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Encriptar contraseña
        user.setAdmin(false);
        
        User savedUser = userService.register(user);
        return ResponseEntity.ok(userMapper.toDto(savedUser));
    }
}
