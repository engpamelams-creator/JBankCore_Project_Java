package br.com.pamela.jbank.modulos.auth.controller;

import br.com.pamela.jbank.infra.security.JwtTokenProvider;
import br.com.pamela.jbank.modulos.auth.dto.AuthDTOs.*;
import br.com.pamela.jbank.modulos.usuarios.domain.User;
import br.com.pamela.jbank.modulos.usuarios.domain.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Tentativa de login para o email: {}", request.email());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            log.info("Login realizado com sucesso para: {}", request.email());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            log.warn("Falha no login para {}: {}", request.email(), e.getMessage());
            throw e; // O GlobalExceptionHandler deve tratar isso (ou retornar 401 aqui direto se preferir)
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequest request) {
        log.info("Recebida solicitação de cadastro para: {}", request.email());

        // 1. Validação de Unicidade (Regra de Negócio)
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de cadastro com email duplicado: {}", request.email());
            return new ResponseEntity<>("Email already in use", HttpStatus.BAD_REQUEST);
        }

        // 2. Construção da Entidade com Segurança
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_USER") // TODO: Extrair para Enum/Constante em refatoração futura
                .build();

        // 3. Persistência
        userRepository.save(user);
        
        log.info("Usuário cadastrado com sucesso. ID: {}", user.getId());

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}
