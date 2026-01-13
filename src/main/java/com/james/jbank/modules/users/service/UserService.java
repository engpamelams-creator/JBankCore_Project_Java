package com.james.jbank.modules.users.service;

import com.james.jbank.modules.users.domain.User;
import com.james.jbank.modules.users.dto.CreateUserDTO;
import com.james.jbank.modules.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Orchestrates the creation of a new user.
     * Checks for duplicates avoids conflict errors from the DB level bubbling up raw.
     */
    @Transactional
    public User createUser(CreateUserDTO dto) {
        if (userRepository.existsByDocument(dto.document())) {
            throw new IllegalArgumentException("User with this document already exists");
        }
        
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // In a real scenario, we would hash the password here.
        User newUser = new User(
            dto.firstName(),
            dto.lastName(),
            dto.document(),
            dto.email(),
            dto.password()
        );

        return userRepository.save(newUser);
    }
}
