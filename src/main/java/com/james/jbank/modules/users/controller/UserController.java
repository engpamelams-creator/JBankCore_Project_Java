package com.james.jbank.modules.users.controller;

import com.james.jbank.modules.users.domain.User;
import com.james.jbank.modules.users.dto.CreateUserDTO;
import com.james.jbank.modules.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> register(@RequestBody @Valid CreateUserDTO dto) {
        User createdUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
