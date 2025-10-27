package com.cineclub.services;

import com.cineclub.entities.User;
import com.cineclub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public User register(User user) {
        return userRepository.save(user);
    }

}
