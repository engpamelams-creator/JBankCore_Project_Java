package br.com.jbank.core.modulos.auth.controller;

import br.com.jbank.core.infra.response.JSendFailResponse;
import br.com.jbank.core.infra.response.JSendSuccessResponse;
import br.com.jbank.core.infra.security.TokenService;
import br.com.jbank.core.modulos.auth.dto.AuthDTOs;
import br.com.jbank.core.modulos.usuarios.domain.User;
import br.com.jbank.core.modulos.usuarios.domain.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<JSendSuccessResponse<AuthDTOs.JwtResponse>> login(@RequestBody @Valid AuthDTOs.LoginRequest data) {
        log.info("Tentativa de login para: {}", data.email());
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        
        var token = tokenService.generateToken((User) auth.getPrincipal());
        
        return ResponseEntity.ok(JSendSuccessResponse.of(new AuthDTOs.JwtResponse(token)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid AuthDTOs.SignupRequest data) {
        log.info("Tentativa de cadastro para: {}", data.email());
        if (userRepository.existsByEmail(data.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(JSendFailResponse.of("Email already exists"));
        }

        User newUser = User.builder()
                .name(data.name())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
                .role("ROLE_USER")
                .build();

        userRepository.save(newUser);
        log.info("Usuário cadastrado com Sucesso: {}", newUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(JSendSuccessResponse.empty());
    }

}
